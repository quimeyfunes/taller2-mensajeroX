/*
 * Servicio.cpp
 *
 *  Created on: 24/3/2015
 *      Author: matias
 */

#include "Servicio.h"

Servicio::Servicio() {
	// TODO Auto-generated constructor stub

}

Servicio::~Servicio() {
	// TODO Auto-generated destructor stub
}

void Servicio::parsearParametros(struct mg_connection *conn){
	char buffer[1000];
	//params es un JSON con toda la data
	int result = mg_get_var(conn, "params", buffer, 1000);
	if(result < 1){
		//TODO: loggear error
		//-1 clave no encontrada
	}

	Json::Value valorParams;
	Json::Reader reader;

	bool parseoExitoso = reader.parse(buffer, valorParams);
	if(parseoExitoso){
		this->parametros = valorParams;
	}else{
		//TODO: loggear error
	}
}

string Servicio::getParametro(string nombreParametro, string valorDefault){
	return this->parametros.get(nombreParametro, valorDefault).asString();
}

Json::Value Servicio::getParametroArray(string nombreParametro, string valorDefault){
	return this->parametros.get(nombreParametro, valorDefault);
}

void Servicio::prueba(){
	cout << "Esto es una prueba." << endl;
}

void Servicio::registrarUsuario(){
	string telefono = this->getParametro(keyTelefono, keyDefault);

	string clave = Usuario::obtenerId(telefono);
	Usuario* user = Usuario::obtener(clave);
	if(user->getId() != keyIdUsuarioNoEncontrado){
		//El usuario ya existe. Devuelvo error
		Loger::getLoger()->warn("Se intento registrar un usuario ya existente. Id: " + clave);
	}else{
		string nombre = this->getParametro(keyNombre, keyDefault);
		string fotoPerfil = this->getParametro(keyFotoDePerfil, keyDefault);

		Usuario* user = new Usuario(nombre, fotoPerfil, telefono);
		user->persistir();

		Loger::getLoger()->info("Se registro el usuario con Id: " + clave);
	}
}

Usuario* Servicio::obtenerUsuario(){

	string telefono = this->getParametro(keyTelefono, keyDefault);
	string clave = Usuario::obtenerId(telefono);
	Usuario* user = Usuario::obtener(clave);

	return user;

}

void Servicio::autenticarUsuario(){

	Usuario* user = this->obtenerUsuario();

	if (user->getId() != keyIdUsuarioNoEncontrado){
		user->setEstadoConexion(Online);
		string token = user->calcularTokenDeSesion();
		user->persistir();
		Loger::getLoger()->info("El usuario "+user->getNombre()+ " inicio sesion correctamente.");
	} else {
		Loger::getLoger()->warn("Usuario "+user->getNombre()+ " no se encuentra registrado en el sistema");
	}
	//TODO: Devolver el token al cliente
	Loger::getLoger()->guardarEstado();
	delete user;
}

void Servicio::administrarPerfil(){

	string nombreUsuario = this->getParametro(keyNombre, keyDefault);
	string estadoDeConexion = this->getParametro(keyEstadoDeConexion, keyDefault);
	string fotoDePerfil = this->getParametro(keyFotoDePerfil, keyDefault);
	bool estado = StringUtil::toBoolean(estadoDeConexion);
	string localizacion = this->getParametro(keyLocalizacion, keyDefault);
	Usuario* user = this->obtenerUsuario();

	if (user->getId() != keyIdUsuarioNoEncontrado){
		user->setNombre(nombreUsuario);
		user->setEstadoConexion(estado);
		user->setFotoDePerfil(fotoDePerfil);
		user->setLocalizacion(localizacion);
		user->persistir();
		Loger::getLoger()->info("Se modificaron los datos del usuario "+user->getNombre()+ " correctamente.");
	} else {
		Loger::getLoger()->warn("El usuario "+user->getNombre()+ " no se encuentra registrado en el sistema");
	}
	Loger::getLoger()->guardarEstado();
	delete user;

}

//TODO: decidir que hacer cuando el usuario no existe.
/*
 * @return true: si el estado de conexion del usuario es conectado.
 * 		  false: si el estado de conexion del usuario es desconectado
 * 		  		 o bien el usuario no existe.
 */
void Servicio::consultarUsuarioOnline() {

	Usuario* user = this->obtenerUsuario();

	if (user->getId() != keyIdUsuarioNoEncontrado) {
		Loger::getLoger()->info("Consulta de estado del usuario "+user->getNombre()+ " exitosa.");
		bool estado = user->getEstadoConexion();
		//TODO: enviar la respuesta al cliente.

	} else {
		Loger::getLoger()->warn(
								"No se pudo obtener el estado del usuario con numero: "
								+ this->getParametro(keyTelefono, keyDefault)
								+ " ya que no se encuentra registrado en el sistema.");
	}

	Loger::getLoger()->guardarEstado();
	delete user;

}


/*
 * Agrega el mensaje que envió el cliente a la conversacion correspondiente y luego la almacena en
 * la Base de Datos.
 *
 */
void Servicio::almacenarConversacion() {

	string idEmisor   = this->getParametro(keyIdUsuarioEmisor,keyDefault);
	string idReceptor = this->getParametro(keyIdUsuarioReceptor,keyDefault);

	//chequeo que los usuarios existan:
	Usuario *emisor   =  Usuario::obtenerPorTelefono(idEmisor);
	Usuario *receptor =  Usuario::obtenerPorTelefono(idReceptor);

	if (	emisor	->getId() 	== keyIdUsuarioNoEncontrado ||
			receptor->getId() 	== keyIdUsuarioNoEncontrado		)
	{
		string msj_warning = "No se pudo almacenar la conversacion porque: ";
		if(emisor->getId()	 == keyIdUsuarioNoEncontrado) msj_warning.append("el emisor no existe.");
		if(receptor->getId() == keyIdUsuarioNoEncontrado) msj_warning.append(" el receptor no existe.");
		Loger::getLoger()->warn(msj_warning);
		Loger::getLoger()->guardarEstado();
	}
	else{
		//Obtengo el mensaje:
		string 		cuerpo 	= this->getParametro(keyCuerpo,keyDefault);
		string 		fecha 	= this->getParametro(keyFecha,keyDefault);
		Mensaje*	mensaje	= new Mensaje(cuerpo,idEmisor,fecha);

		//almaceno la conversacion (si no existe la creo):
		Conversacion *conversacion = Conversacion::obtener(emisor->getId()+"-"+receptor->getId());
		if (conversacion->getId() != keyIdConversacionNoEncontrada) {
			conversacion->agregarMensaje(mensaje);
			conversacion->persistir();
			delete conversacion;
		}
		else{
			vector<Usuario*> usuarios;
			usuarios.push_back(emisor);
			usuarios.push_back(receptor);
			vector<Mensaje*> mensajes;
			mensajes.push_back(mensaje);
			Conversacion *nuevaConversacion = new Conversacion(usuarios,mensajes);
			nuevaConversacion->persistir();
			delete nuevaConversacion;
		}
		delete mensaje;
	}
}

void Servicio::checkinUsuario(){

	Usuario* user = this->obtenerUsuario();
	string localizacion = this->getParametro(keyLocalizacion, keyDefault);

	if (user->getId() != keyIdUsuarioNoEncontrado){
		user->setLocalizacion(localizacion);
		user->persistir();
		Loger::getLoger()->info("Se actualizo la ubicacion del Usuario "+user->getNombre());
	} else {
		Loger::getLoger()->warn("Usuario "+user->getNombre()+ " no se encuentra registrado en el sistema");
	}

	Loger::getLoger()->guardarEstado();
	delete user;

}

void Servicio::desconectarUsuario(){

	Usuario* user = this->obtenerUsuario();

	if (user->getId() != keyIdUsuarioNoEncontrado){
		user->setEstadoConexion(Offline);
		user->persistir();
		Loger::getLoger()->info("El usuario "+user->getNombre()+ " cerro sesion correctamente");
	} else {
		Loger::getLoger()->warn("Usuario "+user->getNombre()+ " no se encuentra registrado en el sistema");
	}

	Loger::getLoger()->guardarEstado();
	delete user;
}

void Servicio::enviarConversacion(){
	string idConversacion = this->getParametro(keyIdConversacion, keyDefault);
	string idUltimoMensaje = this->getParametro(keyIdUltimoMensaje, keyDefault);

	//Puede darse el caso de que la conversacion no exista si este servicio se llamo primero que agregarMensaje
	//No deberia pasar ya que agregarMensaje se llama primero, pero por latencia de red podria llamarse a este servicio primero
	Conversacion* conversacion = Conversacion::obtener(idConversacion);
	if(conversacion->getId() != keyIdConversacionNoEncontrada){
		vector<Mensaje*> mensajes = conversacion->getMensajes();
		vector<Mensaje*> mensajesRespuesta;

		if(idUltimoMensaje != ""){
			bool encontrado = false;
			for(unsigned i=0; i<mensajes.size(); i++){
				if(encontrado){
					mensajesRespuesta.push_back(mensajes[i]);
				}

				if(!encontrado && mensajes[i]->getId() == idUltimoMensaje){
					//A partir del proximo mensaje tengo que enviar todos
					encontrado = true;
				}
			}
		}else{
			//El cliente no tiene mensajes en su aplicacion. Tengo que enviar la conversacion entera
			mensajesRespuesta = mensajes;
		}

		//Todo: Responder al cliente los mensajes. Algunos mensajes o la conversacion entera

	}else{
		Loger::getLoger()->warn("La conversacion "+ idConversacion + " no se encuentra en el sistema");
	}
}

void Servicio::enviarConversaciones(){
	Json::Value idsConversacionesValue = this->getParametroArray(keyIdConversaciones, keyDefault);
	vector<string> idsConversaciones = StringUtil::jsonValueToVector(idsConversacionesValue);

	string idUsuario = this->getParametro(keyIdUsuarioParametro, keyDefault);

	Usuario* usuario = new Usuario(idUsuario);
	if(usuario->getId() != keyIdUsuarioNoEncontrado){
		vector<string> idsConversacionesActuales = usuario->obtnerIdsConversaciones();
		vector<Conversacion*> nuevasConversaciones;

		for(unsigned i=0; i<idsConversacionesActuales.size();i++){
			string idActual = idsConversacionesActuales[i];

			//Si no esta en las conversaciones que me llegan, quiere decir que es una nueva conversacion.
			//Tengo que enviarla al cliente
			if(!StringUtil::vectorContiene(idsConversaciones, idActual)){
				Conversacion* nuevaConversacion = new Conversacion(idActual);
				if(nuevaConversacion->getId() != keyIdConversacionNoEncontrada){
					nuevasConversaciones.push_back(nuevaConversacion);
				}else{
					Loger::getLoger()->warn("La conversacion "+ idActual + " no se encuentra en el sistema");
				}
			}
		}

		//TODO: responder la info de las nuevas conversaciones al cliente: ultimo mensaje, usuario, etc.

	}else{
		Loger::getLoger()->warn("El usuario "+ idUsuario  + " no se encuentra en el sistema");
	}

}
