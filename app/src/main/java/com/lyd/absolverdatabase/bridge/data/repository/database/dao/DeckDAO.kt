package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.DeckType

@Dao
interface DeckDAO {

    @Query("delete from deck_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list: List<Deck>)

    @Query("select * from deck_tb")
    suspend fun getAllDeck() :List<Deck>

    @Query("select * from deck_tb where deckType = :type")
    suspend fun getDecksByDeckType(type :DeckType) :List<Deck>

    @Delete
    suspend fun deleteOneDeck(deck: Deck ):Int
}