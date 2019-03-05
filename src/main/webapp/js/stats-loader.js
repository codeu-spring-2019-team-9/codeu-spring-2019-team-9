var _statsPromise = null;

function getOrPrefetchStats() {
  const url = "/stats";
  if (_statsPromise == null) {
    _statsPromise = fetch(url).then(response => { return response.json(); });
  }
  return _statsPromise;
}

function renderStatsOnPage() {
  getOrPrefetchStats().then(stats => {
    const statsContainer = document.getElementById("stats-container");
    statsContainer.innerHTML = "";
    const messageCountElement = buildStatElement(
      "Message count: " + stats.messageCount
    );
    statsContainer.appendChild(messageCountElement);
  });
}

function buildStatElement(statString) {
  const statElement = document.createElement('p');
  statElement.textContent = statString;
  return statElement;
}