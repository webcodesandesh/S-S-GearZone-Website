const fs = require('fs');
const path = require('path');

const STATIC_DIR = path.join(__dirname, 'shop', 'src', 'main', 'resources', 'static');
const TARGET_DIR = path.join(__dirname, 'shop', 'target', 'classes', 'static');

const ruizRegex = /\/\* ===== RUIZ THEME OVERRIDE[\s\S]*?===== END RUIZ OVERRIDE ===== \*\//g;
const dsOverrideRegex = /\/\* ── DS OVERRIDE: new colors \+ fonts ── \*\/[\s\S]*?\/\* ── END DS OVERRIDE ── \*\//g;
const bodyRegex = /body\s*\{([^\}]*)\}/g;
const fontMuliRegex = /font-family:\s*['"]Muli['"],\s*sans-serif;?/g;

function processHtmlFile(filepath) {
    const originalContent = fs.readFileSync(filepath, 'utf8');
    let content = originalContent;

    // 1. Remove Ruiz Theme
    content = content.replace(ruizRegex, '');

    // 2. Remove DS Override
    content = content.replace(dsOverrideRegex, '');

    // 3. Clean up body CSS which had terrible colors (#0a0a0a on black bg)
    content = content.replace(bodyRegex, (match, p1) => {
        let bodyContent = p1;
        // Fix terrible dark text on dark background
        bodyContent = bodyContent.replace(/color:\s*#0a0a0a\s*;?/g, 'color: #cbd5e1;');
        bodyContent = bodyContent.replace(/color:\s*#000(000)?\s*;?/g, 'color: #cbd5e1;');
        // Also force font choice to Inter in the body explicitly
        if (!bodyContent.includes("font-family: 'Inter'")) {
             bodyContent += " font-family: 'Inter', sans-serif !important; ";
        }
        return `body {${bodyContent}}`;
    });

    // 4. Standardize Font to Inter globally
    content = content.replace(fontMuliRegex, "font-family: 'Inter', sans-serif;");
    content = content.replace(/https:\/\/fonts\.googleapis\.com\/css\?family=Muli:400,600,800,900/g, "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap");
    content = content.replace(/https:\/\/fonts\.googleapis\.com\/css2\?family=Muli:wght@400;600;800;900&display=swap/g, "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap");

    if (content !== originalContent) {
        fs.writeFileSync(filepath, content, 'utf8');
        console.log(`Updated ${path.basename(filepath)}`);
        return true;
    }
    return false;
}

function main() {
    console.log(`Scanning directory: ${STATIC_DIR}...`);
    if (!fs.existsSync(STATIC_DIR)) {
        console.error("Directory not found:", STATIC_DIR);
        return;
    }

    const files = fs.readdirSync(STATIC_DIR);
    const htmlFiles = files.filter(f => f.endsWith('.html')).map(f => path.join(STATIC_DIR, f));
    
    let updatedCount = 0;
    htmlFiles.forEach(file => {
        if (processHtmlFile(file)) {
            updatedCount++;
        }
    });

    console.log(`\nUpdated ${updatedCount} files in source.`);

    // Copy to target directory immediately
    if (!fs.existsSync(TARGET_DIR)) {
        fs.mkdirSync(TARGET_DIR, { recursive: true });
    }

    htmlFiles.forEach(file => {
        const dest = path.join(TARGET_DIR, path.basename(file));
        fs.copyFileSync(file, dest);
    });

    console.log("Synched updated files to target/classes/static/");
}

main();
