
package com.example.monitoreossilos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.monitoreossilos.data.SiloRepository
import com.example.monitoreossilos.model.EstadoSilo
import com.example.monitoreossilos.model.Medicion
import com.example.monitoreossilos.model.Movimiento
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.model.TipoMovimiento
import com.example.monitoreossilos.validation.SiloValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado del formulario para registrar un silo
data class SiloFormState(
    val idSilo: String = "",
    val granjaNombre: String = "",
    val tipoAlimento: String = "",
    val capacidad: String = "",
    val nivelActual: String = "",
    val consumoDiario: String = "",
    val errorId: String? = null,
    val errorGranja: String? = null,
    val errorTipo: String? = null,
    val errorCapacidad: String? = null,
    val errorNivel: String? = null,
    val errorConsumo: String? = null
) {
    val esValido: Boolean
        get() = errorId == null &&
                errorGranja == null &&
                errorTipo == null &&
                errorCapacidad == null &&
                errorNivel == null &&
                errorConsumo == null
}

// Estado del formulario para cargas y descargas
data class MovimientoFormState(
    val tipo: TipoMovimiento = TipoMovimiento.CARGA,
    val cantidad: String = "",
    val observacion: String = "",
    val errorCantidad: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class SiloViewModel(
    private val repository: SiloRepository
) : ViewModel() {

    // Listado de silos
    val silos: StateFlow<List<Silo>> =
        repository.allSilos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtro por estado
    private val _filtroEstado =
        MutableStateFlow<EstadoSilo?>(null)

    val filtroEstado: StateFlow<EstadoSilo?> =
        _filtroEstado.asStateFlow()

    val silosFiltrados: StateFlow<List<Silo>> =
        combine(silos, _filtroEstado) { lista, filtro ->
            if (filtro == null) {
                lista
            } else {
                lista.filter { it.estado == filtro }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onFiltroChange(estado: EstadoSilo?) {
        _filtroEstado.value = estado
    }

    // Alertas de silos críticos
    private val _alertas =
        MutableSharedFlow<Silo>(extraBufferCapacity = 8)

    val alertas: SharedFlow<Silo> =
        _alertas.asSharedFlow()

    fun cargarDatosPrueba() {
        viewModelScope.launch {
            repository.cargarDatosPrueba()

            repository.allSilos.first()
                .filter { it.estado == EstadoSilo.CRITICO }
                .forEach { _alertas.emit(it) }
        }
    }

    // Formulario de registro
    private val _formState =
        MutableStateFlow(SiloFormState())

    val formState: StateFlow<SiloFormState> =
        _formState.asStateFlow()

    fun limpiarFormulario() {
        _formState.value = SiloFormState()
    }

    fun onIdChange(v: String) = _formState.update {
        it.copy(
            idSilo = v,
            errorId = SiloValidator.validarId(v)
        )
    }

    fun onGranjaChange(v: String) = _formState.update {
        it.copy(
            granjaNombre = v,
            errorGranja = SiloValidator.validarTexto(
                v,
                "El nombre de la granja"
            )
        )
    }

    fun onTipoChange(v: String) = _formState.update {
        it.copy(
            tipoAlimento = v,
            errorTipo = SiloValidator.validarTexto(
                v,
                "El tipo de alimento"
            )
        )
    }

    fun onCapacidadChange(v: String) = _formState.update {
        it.copy(
            capacidad = v,
            errorCapacidad =
                SiloValidator.validarCapacidad(v),
            errorNivel = if (it.nivelActual.isBlank()) {
                it.errorNivel
            } else {
                SiloValidator.validarNivel(
                    it.nivelActual,
                    v
                )
            }
        )
    }

    fun onNivelChange(v: String) = _formState.update {
        it.copy(
            nivelActual = v,
            errorNivel = SiloValidator.validarNivel(
                v,
                it.capacidad
            )
        )
    }

    fun onConsumoChange(v: String) = _formState.update {
        it.copy(
            consumoDiario = v,
            errorConsumo =
                SiloValidator.validarConsumo(v)
        )
    }

    // Guardar silo
    fun guardarSilo(): Boolean {
        val f = _formState.value

        val validado = f.copy(
            errorId = SiloValidator.validarId(f.idSilo),
            errorGranja = SiloValidator.validarTexto(
                f.granjaNombre,
                "El nombre de la granja"
            ),
            errorTipo = SiloValidator.validarTexto(
                f.tipoAlimento,
                "El tipo de alimento"
            ),
            errorCapacidad =
                SiloValidator.validarCapacidad(f.capacidad),
            errorNivel = SiloValidator.validarNivel(
                f.nivelActual,
                f.capacidad
            ),
            errorConsumo =
                SiloValidator.validarConsumo(f.consumoDiario)
        )

        _formState.value = validado

        if (!validado.esValido) return false

        val silo = Silo(
            idSilo = f.idSilo.trim().uppercase(),
            granjaNombre = f.granjaNombre.trim(),
            tipoAlimento = f.tipoAlimento.trim(),
            capacidadTotalKg =
                SiloValidator.parsearNumero(f.capacidad)
                    ?: return false,
            nivelActualKg =
                SiloValidator.parsearNumero(f.nivelActual)
                    ?: return false,
            consumoDiarioKg =
                SiloValidator.parsearNumero(f.consumoDiario)
                    ?: return false,
            fechaActualizacion =
                System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insertSilo(silo)

            if (silo.estado == EstadoSilo.CRITICO) {
                _alertas.emit(silo)
            }
        }

        return true
    }

    // Silo seleccionado
    private val _idSeleccionado =
        MutableStateFlow<String?>(null)

    val siloSeleccionado: StateFlow<Silo?> =
        _idSeleccionado.flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                repository.observarSilo(id)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    // Historial de movimientos
    val movimientos: StateFlow<List<Movimiento>> =
        _idSeleccionado.flatMapLatest { id ->
            if (id == null) {
                flowOf(emptyList())
            } else {
                repository.observarMovimientos(id)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Historial de mediciones
    val mediciones: StateFlow<List<Medicion>> =
        _idSeleccionado.flatMapLatest { id ->
            if (id == null) {
                flowOf(emptyList())
            } else {
                repository.observarMediciones(id)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _movimientoForm =
        MutableStateFlow(MovimientoFormState())

    val movimientoForm: StateFlow<MovimientoFormState> =
        _movimientoForm.asStateFlow()

    fun seleccionarSilo(id: String) {
        if (_idSeleccionado.value != id) {
            _idSeleccionado.value = id
            _movimientoForm.value = MovimientoFormState()
        }
    }

    fun onTipoMovimientoChange(
        tipo: TipoMovimiento
    ) = _movimientoForm.update {
        it.copy(
            tipo = tipo,
            errorCantidad = if (it.cantidad.isBlank()) {
                null
            } else {
                SiloValidator.validarCantidadMovimiento(
                    it.cantidad,
                    tipo,
                    siloSeleccionado.value
                )
            }
        )
    }

    fun onCantidadChange(v: String) =
        _movimientoForm.update {
            it.copy(
                cantidad = v,
                errorCantidad =
                    SiloValidator.validarCantidadMovimiento(
                        v,
                        it.tipo,
                        siloSeleccionado.value
                    )
            )
        }

    fun onObservacionChange(v: String) =
        _movimientoForm.update {
            it.copy(observacion = v.take(120))
        }

    // Registrar carga o descarga
    fun registrarMovimiento(): Boolean {
        val silo = siloSeleccionado.value ?: return false
        val f = _movimientoForm.value

        val error =
            SiloValidator.validarCantidadMovimiento(
                f.cantidad,
                f.tipo,
                silo
            )

        _movimientoForm.value =
            f.copy(errorCantidad = error)

        if (error != null) return false

        val cantidad =
            SiloValidator.parsearNumero(f.cantidad)
                ?: return false

        viewModelScope.launch {
            val actualizado =
                repository.registrarMovimiento(
                    silo,
                    f.tipo,
                    cantidad,
                    f.observacion.trim()
                )

            if (actualizado.estado == EstadoSilo.CRITICO) {
                _alertas.emit(actualizado)
            }
        }

        _movimientoForm.value =
            MovimientoFormState(tipo = f.tipo)

        return true
    }

    // Guardar fotografía del silo
    fun guardarFoto(idSilo: String, uri: String) {
        viewModelScope.launch {
            repository.actualizarFoto(idSilo, uri)
        }
    }
}

// Factory para crear el ViewModel con el repositorio
class SiloViewModelFactory(
    private val repository: SiloRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                SiloViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return SiloViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}
