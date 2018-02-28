package moe.mimikyu.game.desktop;

import com.badlogic.gdx.InputAdapter;
import glm_.vec2.Vec2;
import imgui.ImGui;
import imgui.MouseCursor;
import imgui.impl.LwjglGL3;

public class ImGuiInputProcessor extends InputAdapter {
  @Override
  public boolean keyTyped(char character) {
    return false;
  }
  @Override
  public boolean scrolled(int amount) {
    return false;
  }
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    ImGui.INSTANCE.getIo().setMousePos(new Vec2(screenX, screenY));
    boolean[] mouseDown = ImGui.INSTANCE.getIo().getMouseDown();
    mouseDown[button] = true;
    return false;
  }
  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    ImGui.INSTANCE.getIo().setMousePos(new Vec2(screenX, screenY));
    boolean[] mouseDown = ImGui.INSTANCE.getIo().getMouseDown();
    mouseDown[button] = false;
    return false;
  }
  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    ImGui.INSTANCE.getIo().setMousePos(new Vec2(screenX, screenY));
    boolean[] mouseDown = ImGui.INSTANCE.getIo().getMouseDown();
    mouseDown[pointer] = true;
    return false;
  }
  @Override
  public boolean mouseMoved (int screenX, int screenY) {
    ImGui.INSTANCE.getIo().setMousePos(new Vec2(screenX, screenY));
    return false;
  }
  @Override
  public boolean keyDown(int keycode) {
    return false;
  }
  @Override
  public boolean keyUp(int keycode) {
    return false;
  }
}
