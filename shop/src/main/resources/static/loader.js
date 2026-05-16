window.playLoginAnimation = function(callback) {
    if (!document.getElementById("gaming-loader-style")) {
        const style = document.createElement("style");
        style.id = "gaming-loader-style";
        style.innerHTML = `
            #gaming-global-loader {
                position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
                background: #0a0b0f; z-index: 999999;
                display: flex; justify-content: center; align-items: center; flex-direction: column;
                transition: opacity 0.6s ease-out, visibility 0.6s;
                backdrop-filter: blur(10px);
            }
            .helmet-wrapper {
                animation: floatHelmet 3s ease-in-out infinite; margin-bottom: 30px;
            }
            .loading-text { 
                color: #ff6d39; font-family: 'Muli', sans-serif; font-weight: 900; 
                letter-spacing: 6px; font-size: 1.1rem; margin-bottom: 15px;
                animation: pulseGlow 1.5s infinite; text-transform: uppercase;
            }
            .progress-bar-container {
                width: 250px; height: 4px; background: rgba(255,255,255,0.1);
                border-radius: 4px; overflow: hidden; margin: 0 auto; box-shadow: 0 0 10px rgba(0,0,0,0.5);
            }
            .progress-bar-fill {
                height: 100%; width: 0%; background: #ff6d39;
                box-shadow: 0 0 10px #ff6d39;
                animation: fillProgress 3s cubic-bezier(0.1, 0.7, 0.1, 1) forwards;
            }
            @keyframes floatHelmet { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-15px); } }
            @keyframes pulseGlow { 0%, 100% { opacity: 0.5; text-shadow: 0 0 0 rgba(255,109,57,0); } 50% { opacity: 1; text-shadow: 0 0 15px rgba(255,109,57,0.8); } }
            @keyframes fillProgress { 0% { width: 0%; } 20% { width: 30%; } 60% { width: 75%; } 100% { width: 100%; } }
        `;
        document.head.appendChild(style);
    }

    const loader = document.createElement("div");
    loader.id = "gaming-global-loader";
    loader.innerHTML = `
        <div class="helmet-wrapper">
            <svg width="120" height="120" viewBox="0 0 100 100" style="filter: drop-shadow(0 0 20px rgba(255,109,57,0.5));">
                <path d="M50 10 C 20 10 15 40 15 50 C 15 80 20 90 50 90 C 80 90 85 80 85 50 C 85 40 80 10 50 10 Z" fill="#1a1c23" stroke="#ff6d39" stroke-width="3"/>
                <path d="M20 50 L 80 50 A 10 10 0 0 1 80 75 L 20 75 A 10 10 0 0 1 20 50 Z" fill="#0a0b0f" stroke="#ff6d39" stroke-width="2"/>
                <line x1="28" y1="50" x2="28" y2="75" stroke="#ff6d39" stroke-width="2"/>
                <line x1="39" y1="50" x2="39" y2="75" stroke="#ff6d39" stroke-width="2"/>
                <line x1="50" y1="50" x2="50" y2="75" stroke="#ff6d39" stroke-width="2"/>
                <line x1="61" y1="50" x2="61" y2="75" stroke="#ff6d39" stroke-width="2"/>
                <line x1="72" y1="50" x2="72" y2="75" stroke="#ff6d39" stroke-width="2"/>
                <path d="M35 15 L65 15 L60 25 L40 25 Z" fill="#0a0b0f" stroke="#ff6d39" stroke-width="2"/>
            </svg>
        </div>
        <div class="loading-text">Loading Level...</div>
        <div class="progress-bar-container">
            <div class="progress-bar-fill"></div>
        </div>
    `;
    document.body.appendChild(loader);
    
    setTimeout(() => {
        loader.style.opacity = "0";
        loader.style.visibility = "hidden";
        setTimeout(() => {
            const el = document.getElementById("gaming-global-loader");
            if (el) el.remove();
            if (callback) callback();
        }, 600);
    }, 3000);
};
