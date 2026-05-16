const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'shop', 'src', 'main', 'resources', 'static');
const targetDir = path.join(__dirname, 'shop', 'target', 'classes', 'static');

function removeGlassEffect(dir) {
    if (!fs.existsSync(dir)) return;
    const files = fs.readdirSync(dir);
    
    files.forEach(file => {
        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);
        
        if (stat.isDirectory()) {
            removeGlassEffect(fullPath);
        } else if (file.endsWith('.html') || file.endsWith('.css') || file.endsWith('.js')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            // Remove backdrop-filter lines
            let newContent = content.replace(/.*backdrop-filter:\s*blur[^;]+;?/g, '');
            if (newContent !== content) {
                fs.writeFileSync(fullPath, newContent, 'utf8');
                console.log('Removed glass from: ' + fullPath);
            }
        }
    });
}

removeGlassEffect(srcDir);
removeGlassEffect(targetDir);
console.log('Glass effect removed from all static files.');
