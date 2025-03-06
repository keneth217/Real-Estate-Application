package com.keneth.realestateapplication.viewModels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keneth.realestateapplication.data.Appointment
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.data.PropertyCategory
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PropertyViewModel(private val repository: PropertyRepository) : ViewModel() {
    // State for the list of all properties
    private val _propertyLists = mutableStateOf<List<Property>>(emptyList())
    val propertyLists: State<List<Property>> get() = _propertyLists

    private val _propertyCategoryLists = mutableStateOf<List<PropertyCategory>>(emptyList())
    val propertyCategoryLists: State<List<PropertyCategory>> get() = _propertyCategoryLists

    // Loading state for all properties
    private val _isLoadingProperties = mutableStateOf(false)
    val isLoadingProperties: State<Boolean> get() = _isLoadingProperties

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

    // State for total sales

    private val _totalSales = mutableDoubleStateOf(0.0)
    val totalSales: State<Double> = _totalSales

    // State for total sold properties
    private val _totalSoldProperties = mutableIntStateOf(0)
    val totalSoldProperties: State<Int> get() = _totalSoldProperties

    // State for property types
    private val _propertyTypes = mutableStateOf<List<PropertyType>>(emptyList())
    val propertyTypes: State<List<PropertyType>> get() = _propertyTypes

    // Loading state for property types
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    // Success state for operations
    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> get() = _isSuccess

    // Error message state
    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> get() = _errorMessage
    private val _propertyDetails = mutableStateOf<Property?>(null)
    val propertyDetails: State<Property?> get() = _propertyDetails
    private val _appointmentsDetails = mutableStateOf<List<Appointment>>(emptyList())
    val appointmentsDetails: State<List<Appointment>> get() = _appointmentsDetails
    init {
        fetchAllProperties()
        fetchListedProperties()
        fetchSoldProperties()
        fetchPropertyType()
    }



    // Add a new property
    fun addProperty(property: Property, imageUris: List<Uri>) {
        viewModelScope.launch {
            _isLoadingProperties.value = true
            _isSuccess.value = false
            _errorMessage.value = ""

            try {
                val success = repository.addProperty(property, imageUris)
                if (success) {
                    _isSuccess.value = true
                    fetchAllProperties() // Refresh the list
                } else {
                    _errorMessage.value = "Failed to add property. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoadingProperties.value = false
            }
        }
    }
    // Fetch all properties
    fun fetchAllProperties() {
        viewModelScope.launch {
            _isLoadingProperties.value = true
            try {
                _propertyLists.value = repository.getAllProperties()
                _totalProperties.intValue = repository.getTotalProperties()
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching properties: ${e.message}"
            } finally {
                _isLoadingProperties.value = false
            }
        }
    }
    // Fetch listed properties
    fun fetchListedProperties() {
        viewModelScope.launch {
            _isLoadingListedProperties.value = true
            try {
                _listedProperties.value = repository.listProperties()
                _totalListedProperties.intValue = repository.getTotalListedProperties()
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching listed properties: ${e.message}"
            } finally {
                _isLoadingListedProperties.value = false
            }
        }
    }
    // Fetch sold properties
    fun fetchSoldProperties() {
        viewModelScope.launch {
            _isLoadingSoldProperties.value = true
            try {
                _soldProperties.value = repository.listSoldProperties()
                _totalSoldProperties.intValue = repository.getTotalSoldProperties()
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching sold properties: ${e.message}"
            } finally {
                _isLoadingSoldProperties.value = false
            }
        }
    }
    // Fetch property types
    fun fetchPropertyType() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _propertyTypes.value = repository.getPropertyType()
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching property types: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Add a new property type
    fun addPropertyType(propertyType: PropertyType) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addPropertyType(propertyType)
                fetchPropertyType() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Error adding property type: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Get total sales amount
    fun fetchTotalSales() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sales = repository.getTotalSalesAmount()
                _totalSales.value = sales
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching total sales: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchPropertyById(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _propertyDetails.value = repository.getPropertyById(propertyId)
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching total sales: ${e.message}"
                println("Error fetching property details: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun makeAppointment(propertyId: String, appointmentDetails: Appointment) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Call the repository function to make an appointment
                repository.makeAppointment(propertyId, appointmentDetails)
                // Optionally, fetch the updated property details
                val updatedProperty = repository.getPropertyById(propertyId)
                _appointmentsDetails.value = updatedProperty?.appointments!!
            } catch (e: Exception) {
                _errorMessage.value = "Error making appointment: ${e.message}"
                println("Error making appointment: ${e.message}")
            } finally {
                _isLoading.value = false // Reset loading state
            }
        }
    }
    fun listProperty(propertyId:String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.listProperty(propertyId)
                fetchPropertyById(propertyId)
            } catch (e: Exception) {

            } finally {

            }
        }

    }
    fun sellProperty(propertyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Call the repository function to sell the property
                repository.sellProperty(propertyId)
                // Optionally, fetch the updated property details
                val updatedProperty = repository.getPropertyById(propertyId)
                _propertyDetails.value = updatedProperty
            } catch (e: Exception) {
                _errorMessage.value = "Error selling property: ${e.message}"
                println("Error selling property: ${e.message}")
            } finally {
                _isLoading.value = false // Reset loading state
            }
        }
    }
}
