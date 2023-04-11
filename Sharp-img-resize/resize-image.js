import sharp from "sharp";
import { readdirSync } from "fs";
const directory = "./assets";

// see https://web.dev/serve-responsive-images/ for details and
// an alternative in ImageMagick

readdirSync(directory).forEach((file) => {
  sharp(`${directory}/${file}`)
    .resize({ width: 400 })
    .toFormat("webp")
    .toFile(`${directory}/${file}-small.webp`);
});

fs.readdirSync(directory).forEach(async (file) => {
  const metadata = await sharp(`${directory}/${file}`).metadata();
  console.log(`metadata is ${JSON.stringify(metadata)}`);
});
