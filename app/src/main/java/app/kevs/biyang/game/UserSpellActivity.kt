package app.kevs.biyang.game

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kevs.biyang.game.libs.Helper
import app.kevs.biyang.game.libs.TravelumHelper
import app.kevs.biyang.game.libs.data.DataManager
import app.kevs.biyang.game.libs.models.SpellBook
import app.kevs.biyang.game.libs.models.UserSpell
import io.realm.RealmResults
import kotlinx.android.synthetic.main.game_activity_userspells.*
import kotlinx.android.synthetic.main.game_dialog_spell_effect_form.*
import kotlinx.android.synthetic.main.game_dialog_spell_form.*
import kotlinx.android.synthetic.main.game_dialog_spell_form.action_save_changes
import kotlinx.android.synthetic.main.getlist_row_item.view.*

class UserSpellActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity_userspells)
        API = DataManager(this)

        getRecords()

        itemsswipetorefresh.setOnRefreshListener { getRecords() }

        action_create.setOnClickListener { initForm() }
    }

    private fun initForm(userSpell: @ParameterName(name = "spell") UserSpell? = null) {
        TravelumHelper.ShowDialog(this, R.layout.game_dialog_spell_form, 0.8){

            it.apply {

                if (userSpell != null)
                    FormHandler.supplyValues(userSpell, it)

                val dialog = it
                action_apply_defaults.setOnClickListener {FormHandler.applyDefaults(userSpell, dialog)}

                action_save_changes.setOnClickListener {FormHandler.saveChanges(
                    this@UserSpellActivity,
                    API!!,
                    userSpell,
                    dialog
                ){
                    ADAPTER?.notifyDataSetChanged()
                }}

                action_add_effect.setOnClickListener { initSpellEffectForm(txt_json_effect) }
            }

        }
    }

    fun initSpellEffectForm(txtJsonEffect: EditText) {
        TravelumHelper.ShowDialog(this,R.layout.game_dialog_spell_effect_form,0.6){
            it.apply {
                switch_target.setOnCheckedChangeListener { buttonView, isChecked -> if (isChecked) lbl_target.setText("Enemy") else lbl_target.setText("Player") }
                sb_prop.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        when (progress){
                            0 -> lbl_prop.setText("Health")
                            1 -> lbl_prop.setText("Armor")
                            2 -> lbl_prop.setText("Damage")
                            3 -> lbl_prop.setText("SpellDamage")
                            4 -> lbl_prop.setText("attackblock")
                            5 -> lbl_prop.setText("spellblock")
                            6 -> lbl_prop.setText("poisonturn")
                            7 -> lbl_prop.setText("healturn")
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                sb_operator.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        when (progress){
                            0 -> lbl_operator.setText("=")
                            1 -> lbl_operator.setText("+")
                            2 -> lbl_operator.setText("-")
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                sb_value.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        lbl_value.setText(progress.toString())
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                action_save_changes.setOnClickListener {
                    txtJsonEffect.setText("${txtJsonEffect.text}\n${lbl_target.text} ${lbl_prop.text} ${lbl_operator.text} ${lbl_value.text}")
                    this.dismiss()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        API?.dispose()
    }

    var API : DataManager? = null

    var RECORDS : RealmResults<UserSpell>? = null
    set(value) {
        field = value
        if (value != null){
            if (ADAPTER == null)
                ADAPTER = UserSpellAdapter(this, value!!, ::itemClicked, ::itemLongClicked)
            else
                ADAPTER?.notifyDataSetChanged()
        }
    }

    private fun getRecords() {
        itemsswipetorefresh.isRefreshing = true
        API?.transact {
            RECORDS = it.where(UserSpell::class.java).findAll()
            itemsswipetorefresh.isRefreshing = false
        }
    }

    private fun itemClicked(userSpell: @ParameterName(name = "spell") UserSpell) {
        initForm(userSpell)
    }

    private fun itemLongClicked(userSpell: @ParameterName(name = "spell") UserSpell): Boolean {
        Helper.showConfirm(this, "Delete ${userSpell.name} spell?"){
            API?.transact {
                userSpell.deleteFromRealm()
                getRecords()
            }
        }
        return true
    }

    var ADAPTER : UserSpellAdapter? = null
    set(value) {
        field = value
        rv_spells.layoutManager = LinearLayoutManager(this)
        rv_spells.adapter = value
    }
}

class UserSpellVH(view : View) : RecyclerView.ViewHolder(view){
    val image = view.img_thumb
    val name = view.txt_name
    val description = view.txt_description
    val container = view.cv_item_container
}

class UserSpellAdapter(val ctx : Context, val items : RealmResults<UserSpell>,
                       val onClick : (spell : UserSpell) -> Unit,
                       val onLongClick : (spell : UserSpell) -> Boolean)
    : RecyclerView.Adapter<UserSpellVH>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSpellVH {
        return UserSpellVH(LayoutInflater.from(ctx).inflate(R.layout.getlist_row_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: UserSpellVH, position: Int) {
        val item = items.get(position)
        holder?.apply {
            image.visibility = View.INVISIBLE

            name.setText(item!!.name)
            description.setText(item!!.description)
            container.setOnClickListener { onClick(item) }
            container.setOnLongClickListener { onLongClick(item) }
        }
    }

}

object FormHandler{
    fun supplyValues(spell: UserSpell, it: Dialog) {
        it.apply {
            txt_name.setText(spell.name)
            txt_description.setText(spell.description)
            txt_earth.setText(spell.earth.toString())
            txt_wind.setText(spell.wind.toString())
            txt_water.setText(spell.water.toString())
            txt_fire.setText(spell.fire.toString())
            txt_light.setText(spell.light.toString())
            txt_dark.setText(spell.dark.toString())
            txt_json_effect.setText(spell.jsonEffect)
        }
    }

    fun validateForm(userSpell: UserSpell?, it: Dialog): Boolean {
        it.apply {
            if (txt_name.text.isNullOrEmpty()
                ||txt_description.text.isNullOrEmpty()
                ||txt_earth.text.isNullOrEmpty()
                ||txt_wind.text.isNullOrEmpty()
                ||txt_water.text.isNullOrEmpty()
                ||txt_fire.text.isNullOrEmpty()
                ||txt_light.text.isNullOrEmpty()
                ||txt_dark.text.isNullOrEmpty()
                ||txt_json_effect.text.isNullOrEmpty()
            ){
                return false
            }
        }
        return true
    }

    fun applyDefaults(userSpell: UserSpell?, it: Dialog) {
        val spell = SpellBook.spells[0]
        it.apply {
            txt_name.setText(spell.name)
            txt_description.setText(spell.description)
            txt_earth.setText(spell.runes.earth.toString())
            txt_wind.setText(spell.runes.wind.toString())
            txt_water.setText(spell.runes.water.toString())
            txt_fire.setText(spell.runes.fire.toString())
            txt_light.setText(spell.runes.light.toString())
            txt_dark.setText(spell.runes.dark.toString())
        }
    }

    fun saveChanges(ctx : Context, api : DataManager, userSpell: UserSpell?, it: Dialog, onComplete : () -> Unit) {
        if (!validateForm(userSpell, it)) {
            Helper.toast(ctx, "Form incomplete, you may want to apply changes")
        }else{
            var s = UserSpell()
            if (userSpell != null){
                s = userSpell
            }
            it.apply {
                api.transact {
                    s.name = txt_name.text.toString()
                    s.description = txt_description.text.toString()
                    s.earth = txt_earth.text.toString().toInt()
                    s.wind = txt_wind.text.toString().toInt()
                    s.water = txt_water.text.toString().toInt()
                    s.fire = txt_fire.text.toString().toInt()
                    s.light = txt_light.text.toString().toInt()
                    s.dark = txt_dark.text.toString().toInt()
                    s.jsonEffect = txt_json_effect.text.toString()
                    if (userSpell != null)
                        it.insertOrUpdate(userSpell)
                    else
                        it.copyToRealm(s)

                    dismiss()
                    onComplete()
                }
            }
        }
    }

}