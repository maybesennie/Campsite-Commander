package com.example.campsitecommander

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.ArrayList // Use Java list because i'm used to it
import kotlin.random.Random

/**
 * Campsite Commander - FINAL EXAM VERSION
 * TODO: fix the bug where icons dont show for everything if I have time!!
 * Author: Snakhokuhle Dlamini (in a rush)
 */
class MainActivity : AppCompatActivity() {

    // DATA STORAGE: parallel arrays as requested by the prompt
    // used var because i might need to reset them later??
    private var itemNames = ArrayList<String>()
    private var categories = ArrayList<String>()
    private var quantities = ArrayList<Int>()
    private var notes = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // put the starting items in
        addSampleData()
        
        // show splash screen first
        showSplashScreen()
    }

    private fun addSampleData() {
        // basic check so we dont add same stuff twice when hitting back
        if (itemNames.size == 0) {
            itemNames.add(getString(R.string.item_tent))
            categories.add(getString(R.string.cat_shelter))
            quantities.add(1)
            notes.add(getString(R.string.note_tent))

            itemNames.add(getString(R.string.item_stove))
            categories.add(getString(R.string.cat_cooking))
            quantities.add(1)
            notes.add(getString(R.string.note_stove))
        }
    }

    /**
     * SPLASH SCREEN
     */
    private fun showSplashScreen() {
        setContentView(R.layout.activity_main)
        
        val root_view = findViewById<View>(R.id.main)
        if (root_view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root_view) { v, insets ->
                val system_bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(system_bars.left, system_bars.top, system_bars.right, system_bars.bottom)
                insets
            }
        }

        // Easter Egg: click the compass 3 times
        val logo_img = findViewById<ImageView>(R.id.logo)
        var splash_clicks = 0
        logo_img?.setOnClickListener {
            splash_clicks = splash_clicks + 1 // basic way to count lol
            if (splash_clicks == 3) {
                Toast.makeText(this, "The forest whispers... 🌲✨", Toast.LENGTH_SHORT).show()
                splash_clicks = 0
            }
        }

        // 3 second timer then go to home
        Handler(Looper.getMainLooper()).postDelayed({
            showMainScreen()
        }, 3000)
    }

    /**
     * MAIN SCREEN
     */
    private fun showMainScreen() {
        setContentView(R.layout.activity_main_home)

        // hope these IDs match the XML!
        val total_count_txt = findViewById<TextView>(R.id.totalItemsText)
        val tip_label = findViewById<TextView>(R.id.tipTextView)
        val edit_name = findViewById<EditText>(R.id.editItemName)
        val edit_cat = findViewById<EditText>(R.id.editCategory)
        val edit_qty = findViewById<EditText>(R.id.editQuantity)
        val edit_note = findViewById<EditText>(R.id.editNotes)
        
        val btn_add = findViewById<Button>(R.id.btnAddGear)
        val btn_remove = findViewById<Button>(R.id.btnRemoveLast)
        val btn_view = findViewById<Button>(R.id.btnViewList)

        // update the numbers
        run_loop_for_total(total_count_txt)
        set_random_tip(tip_label)

        // long click egg
        total_count_txt.setOnLongClickListener {
            Toast.makeText(this, "🏅 Badge Unlocked!", Toast.LENGTH_LONG).show()
            true
        }

        // ADD BUTTON
        btn_add.setOnClickListener {
            val name_val = edit_name.text.toString().trim()
            val cat_val = edit_cat.text.toString().trim()
            val qty_str = edit_qty.text.toString().trim()
            val note_val = edit_note.text.toString().trim()

            // error check
            if (name_val == "" || cat_val == "" || qty_str == "" || note_val == "") {
                Toast.makeText(this, "Oops! You missed a spot.", Toast.LENGTH_SHORT).show() // Hardcoded oops
            } else {
                val q_int = try { qty_str.toInt() } catch(e: Exception) { 0 }
                if (q_int <= 0) {
                    Toast.makeText(this, "Put a real number!", Toast.LENGTH_SHORT).show()
                } else {
                    // Eggs!
                    if (name_val.lowercase() == "coffee") Toast.makeText(this, "☕ Lifeblood!", Toast.LENGTH_SHORT).show()

                    // save to the lists
                    itemNames.add(name_val)
                    categories.add(cat_val)
                    quantities.add(q_int)
                    notes.add(note_val)
                    
                    run_loop_for_total(total_count_txt)
                    
                    // clean up the inputs
                    edit_name.setText("")
                    edit_cat.setText("")
                    edit_qty.setText("")
                    edit_note.setText("")
                    
                    kill_keyboard()
                    Toast.makeText(this, "Gear stowed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // REMOVE BUTTON
        btn_remove.setOnClickListener {
            if (itemNames.size > 0) {
                val last = itemNames.size - 1
                itemNames.removeAt(last)
                categories.removeAt(last)
                quantities.removeAt(last)
                notes.removeAt(last)
                
                run_loop_for_total(total_count_txt)
                Toast.makeText(this, "Removed it!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing here to remove", Toast.LENGTH_SHORT).show()
            }
        }

        // NAVIGATE
        btn_view.setOnClickListener {
            showDetailedView()
        }
    }

    private fun set_random_tip(txt: TextView?) {
        val tips = arrayOf(
            getString(R.string.tip_1), getString(R.string.tip_2),
            getString(R.string.tip_3), getString(R.string.tip_4),
            getString(R.string.tip_5)
        )
        val r_idx = Random.nextInt(tips.size)
        txt?.text = "Camp Tip: " + tips[r_idx] // manual string build
    }

    private fun run_loop_for_total(textView: TextView) {
        var sum = 0
        // LOOP: index loop for the parallel arrays
        for (i in 0 until quantities.size) {
            val q = quantities.get(i)
            sum = sum + q
        }
        textView.text = "Total Gear: " + sum // hardcoded string prefix oops
    }

    private fun kill_keyboard() {
        val cur = this.currentFocus
        if (cur != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(cur.windowToken, 0)
        }
    }

    /**
     * DETAILED VIEW
     */
    private fun showDetailedView() {
        setContentView(R.layout.activity_detailed_view)

        val list_txt = findViewById<TextView>(R.id.listTextView)
        val back_btn = findViewById<Button>(R.id.btnBack)

        var giant_string = ""
        // LOOP: for loop using indices to match arrays
        for (i in 0..itemNames.size - 1) {
            val n = itemNames[i]
            val c = categories[i]
            val q = quantities[i]
            val note = notes[i]

            var emoji = "🎒"
            if (c.lowercase() == "shelter") emoji = "⛺"
            if (c.lowercase() == "cooking") emoji = "🔥"
            
            // Building the list manually
            giant_string = giant_string + emoji + " " + n.uppercase() + "\n" +
                          " > Cat: " + c + "\n" +
                          " > Qty: " + q + "\n" +
                          " > Note: " + note + "\n" +
                          "---------------\n\n"
        }

        if (giant_string == "") {
            list_txt.text = "Backpack is empty!"
        } else {
            list_txt.text = giant_string
        }

        back_btn.setOnClickListener {
            showMainScreen()
        }
    }
}
