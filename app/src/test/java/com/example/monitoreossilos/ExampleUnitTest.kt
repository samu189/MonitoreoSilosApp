package com.example.monitoreossilos

import com.example.monitoreossilos.model.TipoMovimiento
import com.example.monitoreossilos.ui.MovimientoFormState
import com.example.monitoreossilos.ui.SiloFormState
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    @Test
    fun `SiloFormState es valido cuando no hay errores`() {
        // Prueba 1: Verifica que el formulario es válido si está limpio
        val state = SiloFormState()
        assertTrue(state.esValido)
    }

    @Test
    fun `SiloFormState es invalido cuando hay un error de validacion`() {
        // Prueba 2: Verifica que el formulario detecta errores (ej. en el ID)
        val state = SiloFormState(errorId = "El ID es obligatorio")
        assertFalse(state.esValido)
    }

    @Test
    fun `MovimientoFormState inicia con tipo CARGA por defecto`() {
        // Prueba 3: Verifica que al abrir el movimiento, esté seleccionado "CARGA"
        val state = MovimientoFormState()
        assertEquals(TipoMovimiento.CARGA, state.tipo)
    }

    @Test
    fun `MovimientoFormState registra la cantidad correctamente`() {
        // Prueba 4: Verifica que guarda el número ingresado
        val state = MovimientoFormState(cantidad = "1500")
        assertEquals("1500", state.cantidad)
    }
}