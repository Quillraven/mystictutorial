package io.github.com.quillraven.util;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerTool {
    public static void main(String[] args) {
        // packMap();
        packObjects();
        System.out.println("Texture packing completed successfully!");
    }

    private static void packObjects() {
        String inputDir = "assets_raw/objects";
        String outputDir = "assets/graphics";
        String packFileName = "objects";

        System.out.println("Packing textures from " + inputDir + " to " + outputDir + "/" + packFileName);
        TexturePacker.process(inputDir, outputDir, packFileName);
    }

    private static void packMap() {
        String inputDir = "assets_raw/map";
        String outputDir = "assets/maps";
        String packFileName = "tileset";

        System.out.println("Packing textures from " + inputDir + " to " + outputDir + "/" + packFileName);
        TexturePacker.process(inputDir, outputDir, packFileName);
    }
}
