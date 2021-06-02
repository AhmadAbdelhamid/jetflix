package com.fabler.jetflix.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabler.jetflix.data.network.constant.MoviesApi
import com.fabler.jetflix.domain.model.Movie
import com.fabler.jetflix.domain.repo.MoviesRepository
import com.fabler.jetflix.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingMoviesViewModel @Inject constructor(
  private val repository: MoviesRepository
) : ViewModel() {

  var nowPlayingMovies by mutableStateOf<Resource<List<Movie>>>(Resource.Loading)
    private set

  init {
    fetchNowPlayingMovies()
  }

  fun getTopRatedMovies(): List<Movie>? {
    return when (nowPlayingMovies) {
      is Resource.Error -> null
      Resource.Loading -> null
      is Resource.Success -> (nowPlayingMovies as Resource.Success<List<Movie>>).data
    }
  }

  fun getTopRatedMovieDetails(id: Long): Movie? {
    return when (nowPlayingMovies) {
      is Resource.Error -> null
      Resource.Loading -> null
      is Resource.Success -> (nowPlayingMovies as Resource.Success<List<Movie>>).data.find { it.id == id }
    }
  }

  fun fetchNowPlayingMovies() {
    viewModelScope.launch {
      nowPlayingMovies = Resource.Loading
      val result = repository.getNowPlayingMovies(language = MoviesApi.LANG_ENG, page = 2)
      result.fold(
        { failure ->
          nowPlayingMovies = Resource.Error(failure)
        },
        { data ->
          nowPlayingMovies = Resource.Success(data)
        }
      )
    }
  }
}