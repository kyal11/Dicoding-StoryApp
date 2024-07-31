package com.dicoding.storyapp.data.datasource.remote.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetListStoryResponse(
	@SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@SerializedName("error")
	val error: Boolean,

	@SerializedName("message")
	val message: String
)
@Entity(tableName = "story")
@Parcelize
data class ListStoryItem(
	@SerializedName("photoUrl")
	val photoUrl: String,

	@SerializedName("createdAt")
	val createdAt: String,

	@SerializedName("name")
	val name: String,

	@SerializedName("description")
	val description: String,

	@SerializedName("lon")
	val lon: Double?,

	@PrimaryKey
	@SerializedName("id")
	val id: String,

	@SerializedName("lat")
	val lat: Double?
) : Parcelable

