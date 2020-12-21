package com.raystatic.memestack.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raystatic.memestack.models.GetMemeResponse
import com.raystatic.memestack.other.Constants
import com.raystatic.memestack.other.Resource
import com.raystatic.memestack.repositories.MainRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
):ViewModel(){

    private val _getMemes = MutableLiveData<Resource<GetMemeResponse>>()

    val getMemes:LiveData<Resource<GetMemeResponse>>
        get() = _getMemes

    fun getMemes() = viewModelScope.launch {
        _getMemes.postValue(Resource.loading(null))
        try {
            mainRepository.getMemes().also {
                if (it.isSuccessful){
                    _getMemes.postValue(Resource.success(it.body()))
                }else{
                    _getMemes.postValue(Resource.error(Constants.SOMETHING_WENT_WRONG,null))
                }
            }
        }catch (e:Exception){
            _getMemes.postValue(Resource.error(Constants.SOMETHING_WENT_WRONG, null))
            e.printStackTrace()
        }
    }

}