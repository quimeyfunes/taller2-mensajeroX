/*
 * Localizacion.cpp
 *
 *  Created on: 17/6/2015
 *      Author: juanma
 */

#include "Localizacion.h"
#include <fstream>
#include <iostream>
#include <math.h>

Json::Value Localizacion::lugares = Json::Value(Json::arrayValue);

/**
 * Calcula la distancia entre dos puntos en coordenadas geográficas.
 * @param coordenadasA primer punto.
 * @param coordenadasB segundo punto.
 */
double Localizacion::calcularDistancia(Json::Value coordenadasA,
		Json::Value coordenadasB) {
	double deltaLatitud, deltaLongitud;

	deltaLatitud  = coordenadasA["latitud"].asDouble()
				  - coordenadasB["latitud"].asDouble();
	deltaLongitud = coordenadasA["longitud"].asDouble()
			      - coordenadasB["longitud"].asDouble();

	return sqrt(pow(deltaLatitud,2) + pow(deltaLongitud,2));
}

/**
 * Se encarga de cargar los lugares almacenados en un archivo.
 * @param pathArchivoLugares Es la direccion en disco del archivo donde están
 * almacenados los lugares a ser cargados.
 * @throws runtime_error Si no se pudo abrir el archivo o el formato es incorrecto.
 */
void Localizacion::cargarLugares(string pathArchivoLugares) {
	ifstream archivoLugares;
	archivoLugares.open(pathArchivoLugares.c_str());

	if(!archivoLugares.is_open()){
		throw runtime_error("No se pudo abrir el archivo: "+pathArchivoLugares);
	}

	Json::Reader reader;
	bool parseoExitoso = reader.parse(archivoLugares, lugares);
	if (!parseoExitoso) {
		throw runtime_error("Error al parsear el archivo de lugares: Formato incorrecto.");
	}

	archivoLugares.close();
}