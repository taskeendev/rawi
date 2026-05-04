const statusEl = document.getElementById('status');
const saveBtn = document.getElementById('saveBtn');
const apiUrlInput = document.getElementById('apiUrl');

chrome.storage.local.get('rawiApiUrl', ({ rawiApiUrl }) => {
  apiUrlInput.value = rawiApiUrl || 'http://localhost:8090';
});

apiUrlInput.addEventListener('change', () => {
  chrome.storage.local.set({ rawiApiUrl: apiUrlInput.value.trim() });
});

saveBtn.addEventListener('click', async () => {
  saveBtn.disabled = true;
  statusEl.className = '';
  statusEl.textContent = 'Extracting...';

  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

  let result;
  try {
    [result] = await chrome.scripting.executeScript({
      target: { tabId: tab.id },
      func: extractContent,
    });
  } catch (e) {
    statusEl.className = 'err';
    statusEl.textContent = 'Cannot read this page';
    saveBtn.disabled = false;
    return;
  }

  const { title, content } = result.result;
  const apiUrl = apiUrlInput.value.trim() || 'http://localhost:8090';

  statusEl.textContent = 'Saving...';

  try {
    const res = await fetch(`${apiUrl}/api/v1/content`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ url: tab.url, title, content }),
    });

    if (res.status === 409) {
      statusEl.className = 'err';
      statusEl.textContent = 'Already saved';
    } else if (res.ok) {
      statusEl.className = 'ok';
      statusEl.textContent = '✓ Saved to Rawi';
    } else {
      statusEl.className = 'err';
      statusEl.textContent = `Error ${res.status}`;
    }
  } catch (e) {
    statusEl.className = 'err';
    statusEl.textContent = 'Cannot reach Rawi API';
  }

  saveBtn.disabled = false;
});

function extractContent() {
  const title = document.title;

  const selectors = [
    '[data-ad-comet-preview="message"]',
    '[data-testid="post_message"]',
    'div[dir="auto"] > div[dir="auto"]',
    'article',
    'main',
  ];

  let content = '';
  for (const sel of selectors) {
    const el = document.querySelector(sel);
    if (el && el.innerText.trim().length > 50) {
      content = el.innerText.trim();
      break;
    }
  }

  if (!content) {
    const body = document.body.innerText.trim();
    content = body.length > 5000 ? body.substring(0, 5000) : body;
  }

  return { title, content };
}
