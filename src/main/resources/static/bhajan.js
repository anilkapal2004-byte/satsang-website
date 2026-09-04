console.log("bhajan.js loaded");

// Set this to your deployed backend's URL before going live.
// Locally it falls back to localhost:8080 automatically.
const API_BASE_URL =
  window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1"
    ? "http://localhost:8080"
    : "https://YOUR-PRODUCTION-BACKEND-URL"; // <-- replace with real backend URL when deploying

document.addEventListener("DOMContentLoaded", () => {

  const searchBar = document.getElementById("searchBar");
  const searchBtn = document.getElementById("searchBtn");
  const container = document.getElementById("bhajanContainer");

  // ---------------- SIMILARITY LOGIC ----------------

  function similarity(a, b) {
    let longer = a.length > b.length ? a : b;
    let shorter = a.length > b.length ? b : a;

    longer = longer.toLowerCase();
    shorter = shorter.toLowerCase();

    let longerLength = longer.length;
    if (longerLength === 0) return 1.0;

    return (longerLength - editDistance(longer, shorter)) / longerLength;
  }

  function editDistance(a, b) {
    const costs = [];
    for (let i = 0; i <= a.length; i++) {
      let lastValue = i;
      for (let j = 0; j <= b.length; j++) {
        if (i === 0) costs[j] = j;
        else if (j > 0) {
          let newValue = costs[j - 1];
          if (a.charAt(i - 1) !== b.charAt(j - 1))
            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
          costs[j - 1] = lastValue;
          lastValue = newValue;
        }
      }
      if (i > 0) costs[b.length] = lastValue;
    }
    return costs[b.length];
  }

  // ---------------- SEARCH LOGIC ----------------

  function doSearch() {

    fetch(`${API_BASE_URL}/lyrics/all`)
      .then(res => res.json())
      .then(allBhajans => {

        const query = searchBar.value.toLowerCase().trim();
        container.innerHTML = "";

        if (!query) {
          alert("Please type a bhajan name");
          return;
        }

        const results = allBhajans.filter(b => {
          const title = b.title.toLowerCase();

          if (title.includes(query)) return true;

          const words = title.split(/\s+/);
          let bestScore = 0;

          words.forEach(word => {
            const score = similarity(word, query);
            if (score > bestScore) bestScore = score;
          });

          return bestScore >= 0.5;
        });

        if (results.length === 0) {
          container.innerHTML = "<p style='color:gray;text-align:center;'>No bhajan found 🙏</p>";
          return;
        }

        results.forEach(bhajan => {
          const div = document.createElement("div");
          div.style.background = "#111";
          div.style.color = "white";
          div.style.padding = "25px";
          div.style.margin = "30px 0";
          div.style.borderRadius = "14px";
          div.style.boxShadow = "0 0 20px rgba(214,162,94,0.2)";




          // Extract YouTube video ID if link is present(for showing youtube links in the right side)
          let videoId = null;

if (bhajan.youtubeLink) {

    if (bhajan.youtubeLink.includes("v=")) {

        videoId =
        bhajan.youtubeLink.split("v=")[1].split("&")[0];

    }

    else if (bhajan.youtubeLink.includes("youtu.be/")) {

        videoId =
        bhajan.youtubeLink.split("youtu.be/")[1];

    }
}

div.innerHTML = `

<div class="bhajan-layout">

  <!-- LEFT SIDE -->
  <div class="lyrics-section">

    <h2 style="color:#d6a25e;">
      ${bhajan.title}
    </h2>

    <p><b>Language:</b> ${bhajan.language}</p>

    <pre class="lyrics-text">
${bhajan.lyrics}
    </pre>

  </div>

  <!-- RIGHT SIDE -->
  ${
    videoId
    ? `
      <div class="video-section">

        <iframe
          src="https://www.youtube.com/embed/${videoId}"
          frameborder="0"
          allowfullscreen>
        </iframe>

      </div>
    `
    : ""
  }

</div>
`;

// Append the bhajan div to the container and scroll into view

          container.appendChild(div);
          div.scrollIntoView({ behavior: "smooth" });
        });

      })
      .catch(err => console.error("Backend error:", err));
  }

  searchBtn.addEventListener("click", doSearch);

  searchBar.addEventListener("keydown", e => {
    if (e.key === "Enter") doSearch();
  });

});
