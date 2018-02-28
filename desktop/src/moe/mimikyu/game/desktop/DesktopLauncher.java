package moe.mimikyu.game.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import imgui.Context;
import imgui.impl.LwjglGL3;
import moe.mimikyu.game.Game;
import uno.glfw.GlfwWindow;

public class DesktopLauncher {
  public static void main (String[] arg) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    new Lwjgl3Application(new ApplicationAdapter() {
      private Game game = null;

      @Override
      public void create() {
        long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
        new Context();
        Gdx.input.setInputProcessor(new ImGuiInputProcessor());
        LwjglGL3.INSTANCE.init(new GlfwWindow(windowHandle), false);
        LwjglGL3.INSTANCE.newFrame();

        game = new Game();
      }

      @Override
      public void resize(int width, int height) {
        game.resize(width, height);
      }

      @Override
      public void render() {
        game.render();
      }

      @Override
      public void pause() {
        game.pause();
      }

      @Override
      public void resume() {
        game.resume();
      }

      @Override
      public void dispose() {
        game.cleanup();
      }
    }, config);
  }
}
