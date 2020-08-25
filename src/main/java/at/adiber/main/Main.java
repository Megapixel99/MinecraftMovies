package at.adiber.main;

import at.adiber.commands.CreateCommand;
import at.adiber.commands.RenderCommand;
import at.adiber.commands.StartCommand;
import at.adiber.player.Canvas;
import at.adiber.player.VideoPlayer;
import at.adiber.render.RenderManager;
import at.adiber.render.Video;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {

    public static Main main;
    public HashMap<String, Video> videos = new HashMap<>();
    public HashMap<String, Canvas> canvases = new HashMap<>();
    public HashMap<String, VideoPlayer> players = new HashMap<>();

    public List<RenderManager> renderer = new ArrayList<>();

    @Override
    public void onEnable() {
        main = this;
        getLogger().info("Minecraft Movies loaded.. happy watching!");
        init();
        registerCommands();

        loadAll();
    }

    @Override
    public void onDisable() {
        for(RenderManager man : renderer) {
            getLogger().info("Terminating RenderManager...");
            try {
                man.terminate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        getLogger().info("Minecraft Movies successfully disabled");
    }

    public void init() {
        if(!getDataFolder().exists())
            getDataFolder().mkdir();

        File folder = new File(getDataFolder(), "movies");
        if(!folder.exists())
            folder.mkdir();
    }

    public void registerCommands() {
        getCommand("cc").setExecutor(new CreateCommand());
        getCommand("start").setExecutor(new StartCommand());
        getCommand("render").setExecutor(new RenderCommand());
    }

    public void saveVideo(Video video) {
        File file = new File(getDataFolder(), "saves" + File.separator + "videos" + File.separator + video.getName() + ".ser");
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(video);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCanvas(Canvas canvas) {
        File file = new File(getDataFolder(), "saves" + File.separator + "canvases" + File.separator + canvas.getId() + ".ser");
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(canvas);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAll() {
        File vids = new File(getDataFolder(), "saves" + File.separator + "videos");
        vids.mkdirs();
        try {
            for (final File file : Objects.requireNonNull(vids.listFiles())) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);

                    Video video = (Video) ois.readObject();
                    ois.close();
                    this.videos.put(video.getName(), video);

                    getLogger().info("[vid] +" + video.getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            getLogger().info("[vids] nothing found");
        }

        File cans = new File(getDataFolder(), "saves" + File.separator + "canvases");
        cans.mkdirs();
        try {
            for (final File file : Objects.requireNonNull(cans.listFiles())) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);

                    Canvas canvas = (Canvas) ois.readObject();
                    ois.close();
                    this.canvases.put(canvas.getId(), canvas);

                    getLogger().info("[can] +" + canvas.getId());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            getLogger().info("[cans] nothing found");
        }
    }

}
