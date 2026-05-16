const fs = require('fs');
const path = require('path');

const staticPath = path.join(__dirname, 'src', 'main', 'resources', 'static');
const bgDirPath = path.join(staticPath, 'Bg');

// Target files as requested
const targetFiles = [
    'index.html',
    'login.html',
    'register.html',
    'admin-products.html',
    'admin-orders.html',
    'admin-users.html',
    'products.html'
];

// Read all image names from the Bg folder dynamically
let bgImages = [];
if (fs.existsSync(bgDirPath)) {
    bgImages = fs.readdirSync(bgDirPath).filter(file => file.match(/\.(png|jpe?g|gif|webp)$/i));
}

if (bgImages.length === 0) {
    console.error('No images found in Bg/ folder!');
    process.exit(1);
}

// Convert them to relative web paths using encodeURIComponent for special chars
const bgImagePaths = bgImages.map(img => `'Bg/${encodeURIComponent(img)}'`).join(',\n            ');

const randomBgScript = `\n<!-- RANDOM LOCAL BACKGROUND SCRIPT -->\n<script>\n    document.addEventListener("DOMContentLoaded", function() {\n        var bgImages = [\n            ${bgImagePaths}\n        ];\n        var randomImage = bgImages[Math.floor(Math.random() * bgImages.length)];\n        setTimeout(function() {\n            document.body.style.setProperty('background', '#0B0501 url("' + randomImage + '") center/cover no-repeat', 'important');\n            document.body.style.setProperty('background-attachment', 'fixed', 'important');\n        }, 10);\n    });\n<\/script>`;

function injectRandomBg(filepath) {
    let content = fs.readFileSync(filepath, 'utf8');

    // Strip ALL previous background script injections (any version)
    content = content.replace(/\n?<!-- RANDOM BACKGROUND SCRIPT -->[\s\S]*?<\/script>/gi, '');
    content = content.replace(/\n?<!-- RANDOM LOCAL BACKGROUND SCRIPT -->[\s\S]*?<\/script>/gi, '');

    // Try </body> first, then </html> as fallback
    if (content.includes('</body>')) {
        content = content.replace('</body>', `${randomBgScript}\n</body>`);
    } else if (content.includes('</html>')) {
        content = content.replace('</html>', `${randomBgScript}\n</html>`);
    } else {
        content += randomBgScript;
    }

    fs.writeFileSync(filepath, content, 'utf8');
    return true;
}

let updatedCount = 0;
targetFiles.forEach(filename => {
    const filepath = path.join(staticPath, filename);
    if (fs.existsSync(filepath)) {
        try {
            injectRandomBg(filepath);
            console.log('OK: ' + filename);
            updatedCount++;
        } catch (e) {
            console.error('ERROR: ' + filename + ' - ' + e.message);
        }
    } else {
        console.warn('NOT FOUND: ' + filename);
    }
});

console.log('\nDone! Updated ' + updatedCount + '/' + targetFiles.length + ' files.');
