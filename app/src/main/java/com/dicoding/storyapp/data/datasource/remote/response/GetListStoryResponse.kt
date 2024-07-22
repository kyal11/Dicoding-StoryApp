package com.dicoding.storyapp.data.datasource.remote.response

import com.google.gson.annotations.SerializedName

data class GetListStoryResponse(
	@SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@SerializedName("error")
	val error: Boolean,

	@SerializedName("message")
	val message: String
)

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

	@SerializedName("id")
	val id: String,

	@SerializedName("lat")
	val lat: Double?
)

