package com.example.monitoreossilos.data

import com.example.monitoreossilos.model.Medicion
import com.example.monitoreossilos.model.Movimiento
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SiloRepository(private val dao: SiloDao) {

    val allSilos: Flow<List<Silo>> =
        dao.getAllSilos().map { lista -> lista.map { it.toDomain() } }

    fun observarSilo(id: String): Flow<Silo?> =
        dao.observarSilo(id).map { it?.toDomain() }

    fun observarMovimientos(id: String): Flow<List<Movimiento>> =
        dao.observarMovimientos(id).map { lista -> lista.map { it.toDomain() } }

    fun observarMediciones(id: String): Flow<List<Medicion>> =
        dao.observarMediciones(id).map { lista -> lista.map { it.toDomain() } }

    suspend fun insertSilo(silo: Silo) {
        val existente = dao.getSiloById(silo.idSilo)
        val entity = silo.toEntity().copy(fotoUri = silo.fotoUri ?: existente?.fotoUri)
        dao.insertOrUpdateSilo(entity)
        dao.insertMedicion(
            MedicionEntity(
                idSilo = silo.idSilo,
                nivelKg = silo.nivelActualKg,
                fecha = silo.fechaActualizacion
            )
        )
    }

    suspend fun registrarMovimiento(
        silo: Silo,
        tipo: TipoMovimiento,
        cantidadKg: Double,
        observacion: String
    ): Silo {
        val ahora = System.currentTimeMillis()
        val nuevoNivel = when (tipo) {
            TipoMovimiento.CARGA -> silo.nivelActualKg + cantidadKg
            TipoMovimiento.DESCARGA -> silo.nivelActualKg - cantidadKg
        }
        val actualizado = silo.copy(nivelActualKg = nuevoNivel, fechaActualizacion = ahora)
        dao.insertOrUpdateSilo(actualizado.toEntity())
        dao.insertMovimiento(
            MovimientoEntity(
                idSilo = silo.idSilo,
                tipo = tipo.name,
                cantidadKg = cantidadKg,
                observacion = observacion,
                fecha = ahora
            )
        )
        dao.insertMedicion(MedicionEntity(idSilo = silo.idSilo, nivelKg = nuevoNivel, fecha = ahora))
        return actualizado
    }

    suspend fun actualizarFoto(idSilo: String, uri: String) {
        dao.actualizarFoto(idSilo, uri)
    }

    suspend fun cargarDatosPrueba() {
        val ahora = System.currentTimeMillis()
        val silos = DatosPrueba.generarSilos(ahora)
        dao.borrarMediciones()
        dao.borrarMovimientos()
        dao.insertAllSilos(silos)
        dao.insertAllMediciones(DatosPrueba.generarMediciones(silos, ahora))
    }
}