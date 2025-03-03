package com.keneth.realestateapplication.viewModels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.data.PropertyCategory
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.repository.PropertyRepository
import kotlinx.coroutines.launch

class PropertyViewModel(private val repository: PropertyRepository) : ViewModel() {
    // State for the list of all properties
    private val _propertyLists = mutableStateOf<List<Property>>(emptyList())
    val propertyLists: State<List<Property>> get() = _propertyLists

    // Loading state for all properties
    private val _isLoadingProperties = mutableStateOf(false)
    val isLoadingProperties: State<Boolean> get() = _isLoadingProperties

    // State for the list of property categories
    private val _propertyCategoryLists = mutableStateOf<List<PropertyCategory>>(emptyList())
    val propertyCategoryLists: State<List<PropertyCategory>> get() = _propertyCategoryLists

    // Loading state for property categories
    private val _isLoadingCategories = mutableStateOf(false)
    val isLoadingCategories: State<Boolean> get() = _isLoadingCategories

    // State for the list of listed properties
    private val _listedProperties = mutableStateOf<List<Property>>(emptyList())
    val listedProperties: State<List<Property>> get() = _listedProperties

    // Loading state for listed properties
    private val _isLoadingListedProperties = mutableStateOf(false)
    val isLoadingListedProperties: State<Boolean> get() = _isLoadingListedProperties

    // State for the list of sold properties
    private val _soldProperties = mutableStateOf<List<Property>>(emptyList())
    val soldProperties: State<List<Property>> get() = _soldProperties

    // Loading state for sold properties
    private val _isLoadingSoldProperties = mutableStateOf(false)
    val isLoadingSoldProperties: State<Boolean> get() = _isLoadingSoldProperties

    // State for total properties
    private val _totalProperties = mutableIntStateOf(0)
    val totalProperties: State<Int> get() = _totalProperties

    // State for total listed properties
    private val _totalListedProperties = mutableIntStateOf(0)
    val totalListedProperties: State<Int> get() = _totalListedProperties

    private val _totalSales = mutableDoubleStateOf(0.0)
    val totalSales: State<Double> get() = _totalSales

    // State for total sold properties
    private val _totalSoldProperties = mutableIntStateOf(0)
    val totalSoldProperties: State<Int> get() = _totalSoldProperties


    private val _propertyTypes = mutableStateOf<List<PropertyType>>(emptyList())
    val propertyTypes: State<List<PropertyType>> get() = _propertyTypes

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> get() = _isSuccess


    init {
        fetchListedProperties()
        fetchAllProperties()
        fetchSoldProperties()
        fetchPropertyType()

    }

    // Add a property
    fun addProperty(property: Property, imageUris: List<Uri>) {
        viewModelScope.launch {
            try {
                _isLoadingProperties.value = true
                val success = repository.addProperty(property,imageUris)
                if (success) {
                    _isSuccess.value = true
                    fetchAllProperties()
                }
                _isLoadingProperties.value = false
            } catch (e: Exception) {
                _isSuccess.value = false

            }
        }
    }

    // Fetch all properties
    fun fetchAllProperties() {
        viewModelScope.launch {
            _isLoadingProperties.value = true
            _propertyLists.value = repository.getAllProperties()
            _totalProperties.intValue = repository.getTotalProperties()
            _isLoadingProperties.value = false
        }
    }

    // Fetch listed properties
    fun fetchListedProperties() {
        viewModelScope.launch {
            _isLoadingListedProperties.value = true // Set loading state
            try {
                // Fetch listed properties from the repository
                _listedProperties.value = repository.listProperties()

                // Fetch total listed properties (if needed)
                _totalListedProperties.value = repository.getTotalListedProperties()

                println("Total Listed in ViewModel: ${_listedProperties.value.size}")
                println("All Listed in ViewModel: ${_listedProperties.value}")
            } catch (e: Exception) {
                // Log the error and update the state
                println("Error fetching listed properties in ViewModel: ${e.message}")
                _listedProperties.value = emptyList() // Reset to empty list on error
            } finally {
                _isLoadingListedProperties.value = false // Reset loading state
            }
        }
    }

    // Fetch sold properties
    fun fetchSoldProperties() {
        viewModelScope.launch {
            _isLoadingSoldProperties.value = true
            _soldProperties.value = repository.listSoldProperties()
            _totalSoldProperties.intValue = repository.getTotalSoldProperties()
            _isLoadingSoldProperties.value = false
        }
    }

    // Add a new property type
    fun addPropertyType(property: PropertyType) {
        viewModelScope.launch {
            try {
                _isLoading.value = true // Show loading state
                repository.addPropertyType(property) // Add property type
                fetchPropertyType() // Fetch updated list
            } catch (e: Exception) {
                // Handle errors (e.g., show a message to the user)
                println("Error adding property type: ${e.message}")
            } finally {
                _isLoading.value = false // Hide loading state
            }
        }
    }

    // Fetch property types
    fun fetchPropertyType() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _propertyTypes.value =
                    repository.getPropertyType() // Update state with fetched data
            } catch (e: Exception) {
                // Handle errors (e.g., show a message to the user)
                println("Error fetching property types: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTotalSales() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _totalSales.value=repository.getTotalSalesAmount()

            }catch (e: Exception) {
                e.message?.let { error(it) }
            }
        }
    }

}
