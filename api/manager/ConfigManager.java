package me.vaimok.nekoplus.api.manager;

import com.google.gson.*;
import me.vaimok.nekoplus.client.Client;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Bind;
import me.vaimok.nekoplus.client.setting.EnumConverter;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.util.moduleUtil.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public
class ConfigManager implements Util {

    public ArrayList <Client> features = new ArrayList <> ( );
    public String config = "nekoplus/config/";

    //TODO: String[] Array for FriendList? Also ENUMS!!!!!
    public static
    void setValueFromJson (Client feature , Setting setting , JsonElement element ) {
        switch (setting.getType ( )) {
            case "Boolean":
                setting.setValue ( element.getAsBoolean ( ) );
                break;
            case "Double":
                setting.setValue ( element.getAsDouble ( ) );
                break;
            case "Float":
                setting.setValue ( element.getAsFloat ( ) );
                break;
            case "Integer":
                setting.setValue ( element.getAsInt ( ) );
                break;
            case "String":
                String str = element.getAsString ( );
                setting.setValue ( str.replace ( "_" , " " ) );
                break;
            case "Bind":
                setting.setValue ( new Bind.BindConverter ( ).doBackward ( element ) );
                break;
            case "Enum":
                try {
                    EnumConverter converter = new EnumConverter ( ( (Enum) setting.getValue ( ) ).getClass ( ) );
                    Enum value = converter.doBackward ( element );
                    setting.setValue ( value == null ? setting.getDefaultValue ( ) : value );
                    break;
                } catch ( Exception e ) {
                    break;
                }
            default:
                nekoplus.LOGGER.error ( "Unknown Setting type for: " + feature.getName ( ) + " : " + setting.getName ( ) );
        }
    }

    private static
    void loadFile ( JsonObject input , Client feature ) {
        for (Map.Entry < String, JsonElement > entry : input.entrySet ( )) {
            String settingName = entry.getKey ( );
            JsonElement element = entry.getValue ( );
            if ( feature instanceof FriendManager ) {
                try {
                    nekoplus.friendManager.addFriend ( new FriendManager.Friend ( element.getAsString ( ) , UUID.fromString ( settingName ) ) );
                } catch ( Exception e ) {
                    e.printStackTrace ( );
                }
            } else {
                boolean settingFound = false;
                for (Setting setting : feature.getSettings ( )) {
                    if ( settingName.equals ( setting.getName ( ) ) ) {
                        try {
                            setValueFromJson ( feature , setting , element );
                        } catch ( Exception e ) {
                            e.printStackTrace ( );
                        }
                        settingFound = true;
                    }
                }

                if ( settingFound ) {
                    continue;
                }
            }
        }
    }

    public
    void loadConfig ( String name ) {
        List < File > files = Arrays.stream ( Objects.requireNonNull ( new File ( "nekoplus" ).listFiles ( ) ) )
                .filter ( File::isDirectory )
                .collect ( Collectors.toList ( ) );
        if ( files.contains ( new File ( "nekoplus/" + name + "/" ) ) ) {
            config = "nekoplus/" + name + "/";
        } else {
            config = "nekoplus/config/";
        }
        nekoplus.friendManager.onLoad ( );
        for (Client feature : this.features) {
            try {
                loadSettings ( feature );
            } catch ( IOException e ) {
                e.printStackTrace ( );
            }
        }
        saveCurrentConfig ( );
    }

    public
    void saveConfig ( String name ) {
        config = "nekoplus/" + name + "/";
        File path = new File ( config );
        if ( ! path.exists ( ) ) {
            path.mkdir ( );
        }
        nekoplus.friendManager.saveFriends ( );
        for (Client feature : this.features) {
            try {
                saveSettings ( feature );
            } catch ( IOException e ) {
                e.printStackTrace ( );
            }
        }
        saveCurrentConfig ( );
    }

    public
    void saveCurrentConfig ( ) {
        File currentConfig = new File ( "nekoplus/currentconfig.txt" );
        try {
            if ( currentConfig.exists ( ) ) {
                FileWriter writer = new FileWriter ( currentConfig );
                String tempConfig = config.replaceAll ( "/" , "" );
                writer.write ( tempConfig.replaceAll ( "nekoplus" , "" ) );
                writer.close ( );
            } else {
                currentConfig.createNewFile ( );
                FileWriter writer = new FileWriter ( currentConfig );
                String tempConfig = config.replaceAll ( "/" , "" );
                writer.write ( tempConfig.replaceAll ( "nekoplus" , "" ) );
                writer.close ( );
            }
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    public
    String loadCurrentConfig ( ) {
        File currentConfig = new File ( "nekoplus/currentconfig.txt" );
        String name = "config";
        try {
            if ( currentConfig.exists ( ) ) {
                Scanner reader = new Scanner ( currentConfig );
                while ( reader.hasNextLine ( ) ) {
                    name = reader.nextLine ( );
                }
                reader.close ( );
            }
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
        return name;
    }

    public
    void resetConfig ( boolean saveConfig , String name ) {
        for (Client feature : this.features) {
            feature.reset ( );
        }
        if ( saveConfig ) saveConfig ( name );
    }

    public
    void saveSettings ( Client feature ) throws IOException {
        JsonObject object = new JsonObject ( );
        File directory = new File ( config + getDirectory ( feature ) );
        if ( ! directory.exists ( ) ) {
            directory.mkdir ( );
        }
        String featureName = config + getDirectory ( feature ) + feature.getName ( ) + ".json";
        Path outputFile = Paths.get ( featureName );
        if ( ! Files.exists ( outputFile ) ) {
            Files.createFile ( outputFile );
        }
        Gson gson = new GsonBuilder ( ).setPrettyPrinting ( ).create ( );
        String json = gson.toJson ( writeSettings ( feature ) );
        BufferedWriter writer = new BufferedWriter ( new OutputStreamWriter ( Files.newOutputStream ( outputFile ) ) );
        writer.write ( json );
        writer.close ( );
    }

    //TODO: add everything with Settings here
    public
    void init ( ) {
        features.addAll ( ModuleManager.modules );
        features.add ( nekoplus.friendManager );

        String name = loadCurrentConfig ( );
        loadConfig ( name );
        nekoplus.LOGGER.info ( "Config loaded." );
    }

    private
    void loadSettings ( Client feature ) throws IOException {
        String featureName = config + getDirectory ( feature ) + feature.getName ( ) + ".json";
        Path featurePath = Paths.get ( featureName );
        if ( ! Files.exists ( featurePath ) ) {
            return;
        }
        loadPath ( featurePath , feature );
    }

    private
    void loadPath ( Path path , Client feature ) throws IOException {
        InputStream stream = Files.newInputStream ( path );
        try {
            loadFile ( new JsonParser ( ).parse ( new InputStreamReader ( stream ) ).getAsJsonObject ( ) , feature );
        } catch ( IllegalStateException e ) {
            nekoplus.LOGGER.error ( "Bad Config File for: " + feature.getName ( ) + ". Resetting..." );
            loadFile ( new JsonObject ( ) , feature );
        }
        stream.close ( );
    }

    public
    JsonObject writeSettings ( Client feature ) {
        JsonObject object = new JsonObject ( );
        JsonParser jp = new JsonParser ( );
        for (Setting setting : feature.getSettings ( )) {
            if ( setting.isEnumSetting ( ) ) {
                EnumConverter converter = new EnumConverter ( ( (Enum) setting.getValue ( ) ).getClass ( ) );
                object.add ( setting.getName ( ) , converter.doForward ( (Enum) setting.getValue ( ) ) );
                continue;
            }

            if ( setting.isStringSetting ( ) ) {
                String str = (String) setting.getValue ( );
                setting.setValue ( str.replace ( " " , "_" ) );
            }

            try {
                object.add ( setting.getName ( ) , jp.parse ( setting.getValueAsString ( ) ) );
            } catch ( Exception e ) {
                e.printStackTrace ( );
            }

        }
        return object;
    }

    public
    String getDirectory ( Client feature ) {
        String directory = "";
        if ( feature instanceof Module ) {
            directory = directory + ( (Module) feature ).getCategory ( ).getName ( ) + "/";
        }
        return directory;
    }
}
