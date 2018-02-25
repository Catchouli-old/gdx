package moe.mimikyu.game

import java.util.Base64

import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.{Matrix4, Vector2}
import com.badlogic.gdx.scenes.scene2d.Actor

class Game {
  val batch = new SpriteBatch
  val guiFont = new BitmapFont
  val camera = new OrthographicCamera(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  val identity = new Matrix4

  var playerPos = new Vector2(192, -192)
  var playerVel = new Vector2(0, 0)

  val lvl =
    """
      |JJJJJJJJJJJJJJJJJJJJJJJJJJJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JAAAAAAAAAAAAAAAAAAAAAAAAAJ
      |JJJJJJJJJJJJJJJJJJJJJJJJJJJ
  """.stripMargin.trim.split("\n").map(_.replace("\r", "")).map(_.map(decodeTile))

  val tileWidth = 32
  val tileset = TextureRegion.split(new Texture("fantasy-tileset.png"), tileWidth, tileWidth).flatten

  def decodeTile(c: Char) = {
    val charMap = Seq('A' to 'Z', 'a' to 'z', '0' to '9', Seq('+', '/')).flatten.zip(0 to 63).toMap
    charMap(c)
  }

  def update = {
    val delta = Gdx.graphics.getDeltaTime

    val jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.UP)
    val leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT)
    val rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT)

    // todo: different friction when character is touching a floor
    // todo: collide with floors and reset jumping

    val acc = 150.0f
    val restitution = 20.0f
    val maxFallSpeed = 1000.0f
    val accX = if (leftPressed && !rightPressed) -acc else if (rightPressed && !leftPressed) acc else 0.0f
    val accY = if (jumpPressed && playerPos.y == -192.0f) 500.0f else -25.0f

    playerVel.x = (playerVel.x + accX) * (1.0f - delta * restitution)
    playerVel.y = (playerVel.y + accY)

    playerVel.y = Math.signum(playerVel.y) * Math.min(Math.abs(playerVel.y), maxFallSpeed)

    playerPos.x = playerPos.x + playerVel.x * delta
    playerPos.y = playerPos.y + playerVel.y * delta

    if (playerPos.y < -192.0f) {
      playerPos.y = -192.0f
      playerVel.y = 0.0f
    }
  }

  def render = {
    update

    // Main render
    camera.position.x = playerPos.x
    camera.position.y = playerPos.y
    camera.update
    batch.setProjectionMatrix(camera.combined)

		Gdx.gl.glClearColor(0, 0, 0, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		batch.begin
    // Draw level
    for (y <- lvl.indices) {
      val row = lvl(y)
      for (x <- row.indices) {
        batch.draw(tileset(lvl(y)(x)), x * tileWidth, -y * tileWidth)
      }
    }
    // Draw player
    batch.draw(tileset(156), playerPos.x, playerPos.y)
		batch.end

    // Gui render
    batch.setProjectionMatrix(identity)
    batch.begin
    guiFont.draw(batch, "hello", 10, 10)
    batch.end
  }

  def cleanup = {
    batch.dispose
    tileset(0).getTexture.dispose
  }
}
