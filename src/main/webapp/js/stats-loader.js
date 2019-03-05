function fetchStats() {
  const url = "/stats";
  fetch(url)
    .then(response => {
      return response.json();
    })
    .then(stats => {
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

