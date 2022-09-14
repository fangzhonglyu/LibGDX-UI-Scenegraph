/*
 * TextureParser.java
 *
 * This is an interface for parsing a JSON entry into a TextureParser asset. Texture
 * assets have multiple values, all corresponding to the class TextureLoader.TextureParameter.
 * These names and format of these values in the JSON should correspond to their use
 * in that class.
 *
 * @author Walker M. White
 * @data   04/20/2020
 */
package edu.cornell.gdiac.ui.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * This class parses a JSON entry into a {@link Texture}.
 *
 * The parser converts JSON entries into {@link TextureLoader.TextureParameter}
 * values of the same name. It is also possible to specify a texture by simply
 * giving the name of the file.  In that case, the default parameters will be
 * used on loading.
 * 
 * all properties) are stored in the file.
 */
public class TextureParser implements AssetParser<Texture> {
    /** The current font entry in the JSON directory */
    private JsonValue root;

    /**
     * Returns the asset type generated by this parser
     *
     * @return the asset type generated by this parser
     */
    public Class<Texture> getType() {
        return Texture.class;
    }

    /**
     * Resets the parser iterator for the given directory.
     *
     * The value directory is assumed to be the root of a larger JSON structure.
     * The individual assets are defined by subtrees in this structure.
     *
     * @param directory    The JSON representation of the asset directory
     */
    public void reset(JsonValue directory) {
        root = directory;
        root = root.getChild( "textures" );
    }

    /**
     * Returns true if there are still assets left to generate
     *
     * @return true if there are still assets left to generate
     */
    public boolean hasNext() {
        return root != null;
    }

    /**
     * Processes the next available texture, loading it into the asset manager
     *
     * The parser converts JSON entries into {@link TextureLoader.TextureParameter}
     * values of the same name. The file will be the contents of the file entry.  The 
     * key will be the name of the font object.
     *
     * If the JSON value is a string and not an object, it will interpret that
     * string as the file and use the default settings.
     *
     * This method fails silently if there are no available assets to process.
     *
     * @param manager	The asset manager to load an asset
     * @param keymap    The mapping of JSON keys to asset file names
     */
    public void processNext(AssetManager manager, ObjectMap<String,String> keymap) {
        TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
        if (root.isString()) {
            params.genMipMaps = false;
            params.minFilter = Texture.TextureFilter.Linear;
            params.magFilter = Texture.TextureFilter.Linear;
            params.wrapU = Texture.TextureWrap.ClampToEdge;
            params.wrapV = Texture.TextureWrap.ClampToEdge;
            params.format = Pixmap.Format.RGBA8888;
            String file = root.asString();
            keymap.put(root.name(),file);
            manager.load( file,Texture.class, params );
		} else {
            params.genMipMaps = root.getBoolean( "mipmaps",false );
            params.minFilter = ParserUtils.parseFilter(root.get("minFilter"), Texture.TextureFilter.Linear);
            params.magFilter = ParserUtils.parseFilter(root.get("magFilter"), Texture.TextureFilter.Linear);
            params.wrapU = ParserUtils.parseWrap(root.get("wrapU"), Texture.TextureWrap.ClampToEdge);
            params.wrapV = ParserUtils.parseWrap(root.get("wrapV"), Texture.TextureWrap.ClampToEdge);
            params.format = root.getBoolean( "alpha",true ) ? Pixmap.Format.RGBA8888 : Pixmap.Format.RGB888;
            String file = root.getString( "file", null );
            if (file != null) {
                keymap.put(root.name(),file);
                manager.load( file,Texture.class, params );
            }
		}
        root = root.next();
    }

    /**
     * Returns true if o is another TextureParser
     *
     * @return true if o is another TextureParser
     */
    public boolean equals(Object o) {
        return o instanceof TextureParser;
    }


}
