package moe.mimikyu.game

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.{Matrix4, Vector2}
import com.badlogic.gdx.{Gdx, Input}
import imgui.impl.LwjglGL3
import imgui.{ImGui, TreeNodeFlags}

class Game {
  // Rendering and framework stuff
  val identity = new Matrix4
  val camera = new OrthographicCamera(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  val batch = new SpriteBatch
  val guiFont = new BitmapFont
  val imgui = ImGui.INSTANCE

  // Game state
  val gameState = new Object {
    var gravity = 25.0f

    val player = new Object {
      var position = new Vector2(192, -64)
      var velocity = new Vector2(0, 0)
      var acceleration = 200.0f
      var friction = 20.0f
      var mass: Option[Float] = None
      var maxSpeed = 1000.0f
      var jumpSpeed = 500.0f
    }
  }

  val lvl = new Map("""
      |JJJJJJJJJJJJJJJJJJJJJJJJJJJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JJJJJJJJJJJJJJJJJJJJJJJJJJJ
  """)

  val tileWidth = 32
  val tileset = TextureRegion.split(new Texture("fantasy-tileset.png"), tileWidth, tileWidth).flatten

  def update = {
    val s = gameState

    val delta = Gdx.graphics.getDeltaTime

    val jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.UP)
    val leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT)
    val rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT)

    // todo: different friction when character is touching a floor
    // todo: collide with floors and reset jumping

    val accX = if (leftPressed && !rightPressed) -s.player.acceleration
               else if (rightPressed && !leftPressed) s.player.acceleration
               else 0.0f
    val accY = if (jumpPressed && s.player.position.y == -192.0f) s.player.jumpSpeed else -s.gravity

    s.player.velocity.x = (s.player.velocity.x + accX) * (1.0f - delta * s.player.friction)
    s.player.velocity.y = (s.player.velocity.y + accY)

    s.player.velocity.y = Math.signum(s.player.velocity.y) *
       Math.min(Math.abs(s.player.velocity.y), s.player.maxSpeed)

    s.player.position.x = s.player.position.x + s.player.velocity.x * delta
    s.player.position.y = s.player.position.y + s.player.velocity.y * delta

    if (s.player.position.y < -192.0f) {
      s.player.position.y = -192.0f
      s.player.velocity.y = 0.0f
    }
  }

  def render = {
    imgui.newFrame
    update

    // Main render
    camera.position.x = gameState.player.position.x
    camera.position.y = gameState.player.position.y
    camera.update
    batch.setProjectionMatrix(camera.combined)

		Gdx.gl.glClearColor(0, 0, 0, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		batch.begin
    // Draw level
    for (y <- lvl.rows.indices) {
      val row = lvl.rows(y)
      for (x <- row.indices) {
        batch.draw(tileset(lvl.rows(y)(x)), x * tileWidth, -y * tileWidth)
      }
    }
    // Draw player
    batch.draw(tileset(156), gameState.player.position.x, gameState.player.position.y)
		batch.end

    // Gui render
    batch.setProjectionMatrix(identity)
    batch.begin
    guiFont.draw(batch, "hello", 10, 10)
    batch.end

    drawImgui
  }

  def drawImgui = {
    val s = gameState

    // Copy values to array references
    val gravityRef = Array(s.gravity)
    val playerPos = Array(s.player.position.x, s.player.position.y)
    val playerVel = Array(s.player.velocity.x, s.player.velocity.y)
    val playerAcc = Array(s.player.acceleration)
    val playerFriction = Array(s.player.friction)
    val playerMass = Array(s.player.mass match { case None => 0.0f; case Some(m) => m })

    imgui.begin("Debug", null, 0)
      if (imgui.collapsingHeader("Movement", TreeNodeFlags.DefaultOpen.getI())) {
        imgui.dragFloat("Gravity", gravityRef, 0.1f, -100.0f, 100.0f, "%f", 1.0f)
        if (imgui.collapsingHeader("Player physics", TreeNodeFlags.DefaultOpen.getI())) {
          imgui.dragFloat2("Position", playerPos, 1.0f, -10000.0f, 10000.0f, "%f", 1.0f)
          imgui.dragFloat2("Velocity", playerVel, 1.0f, -10000.0f, 10000.0f, "%f", 1.0f)
          imgui.dragFloat("Acceleration", playerAcc, 1.0f, -1000.0f, 1000.0f, "%f", 1.0f)
          imgui.dragFloat("Friction", playerFriction, 0.1f, -1000.0f, 1000.0f, "%f", 1.0f)
          imgui.dragFloat("Mass", playerMass, 0.1f, 0.0f, 1000.0f, "%f", 1.0f)
        }
      }
    imgui.end()

    // Copy back
    s.gravity = gravityRef(0)
    s.player.position.x = playerPos(0)
    s.player.position.y = playerPos(1)
    s.player.velocity.x = playerVel(0)
    s.player.velocity.y = playerVel(1)
    s.player.acceleration = playerAcc(0)
    s.player.friction = playerFriction(0)
    s.player.mass = playerMass(0) match { case 0.0f => None; case m: Float => Some(m) }

    // Render imgui
    imgui.render()
    val drawData = imgui.getDrawData()
    if (drawData != null)
      LwjglGL3.INSTANCE.renderDrawData(drawData)
  }

  def cleanup = {
    batch.dispose
    tileset(0).getTexture.dispose
  }

  def resize(x: Int, y: Int) = {
    camera.setToOrtho(false, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  }

  def pause = {}
  def resume = {}
}
