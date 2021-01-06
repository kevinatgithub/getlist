package app.kevs.biyang.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.game_home.*

class GameDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_home)

        imgCards.setOnClickListener { gotoCards() }
        lblCards.setOnClickListener { gotoCards() }

        imgSpells.setOnClickListener { gotoSpells() }
        lblSpells.setOnClickListener { gotoSpells() }

        imgDuelAI.setOnClickListener { gotoDuelAI() }
        lblDuelAI.setOnClickListener { gotoDuelAI() }
    }

    private fun gotoDuelAI() {
        startActivity(Intent(this, AICharacterActivity::class.java))
    }

    private fun gotoSpells() {
        startActivity(Intent(this, UserSpellActivity::class.java))
    }

    private fun gotoCards() {
        startActivity(Intent(this, MonsterCardsActivity::class.java))
    }
}