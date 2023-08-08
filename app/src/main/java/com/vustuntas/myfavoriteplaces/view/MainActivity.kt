package com.vustuntas.myfavoriteplaces.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.vustuntas.myfavoriteplaces.R
import com.vustuntas.myfavoriteplaces.adapter.RecyclerAdapterPlace
import com.vustuntas.myfavoriteplaces.databinding.ActivityMainBinding
import com.vustuntas.myfavoriteplaces.model.Places
import com.vustuntas.myfavoriteplaces.roomDb.PlaceDao
import com.vustuntas.myfavoriteplaces.roomDb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var compositeDisposable = CompositeDisposable()
    private lateinit var placeDatabase : PlaceDatabase
    private lateinit var placeDao : PlaceDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.menu_toolBar))
        placeDatabase = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        placeDao = placeDatabase.placeDao()
        compositeDisposable.add(
            placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)


        )

    }
    private fun handleResponse(placeList : List<Places>){
        binding.recyclerView.adapter = RecyclerAdapterPlace(placeList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val infalter = menuInflater
        infalter.inflate(R.menu.menu_options,menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_optionsMenu_addPlace){
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("optionsMenu","optionsMenu")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}