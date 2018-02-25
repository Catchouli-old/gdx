package moe.mimikyu.game

import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Entry extends ApplicationAdapter {
  var game: Game = null
  override def create = { game = new Game() }
  override def render = game.render
  override def dispose = game.cleanup
}
