package io.github.com.quillraven.util;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import java.io.File;

/**
 * Utility class to execute LibGDX's TextureAtlas packer tool.
 * This tool packs multiple images into a single texture atlas.
 */
public class TexturePackerTool {
    public static void main(String[] args) {
        String inputDir = "assets_raw/map";
        String outputDir = "assets/maps";
        String packFileName = "tileset";

        System.out.println("Packing textures from " + inputDir + " to " + outputDir + "/" + packFileName);
        TexturePacker.process(inputDir, outputDir, packFileName);
        System.out.println("Texture packing completed successfully!");
    }
}
