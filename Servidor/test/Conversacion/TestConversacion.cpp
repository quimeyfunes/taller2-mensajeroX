/*
 * TestUsuario.cpp
 *
 *  Created on: 30/3/2015
 *      Author: matias
 */

#include "TestConversacion.h"

CPPUNIT_TEST_SUITE_REGISTRATION(TestConversacion);

TestConversacion::TestConversacion() {
	BaseDeDatos::setPath(path_BaseDeDatosTests);
}

TestConversacion::~TestConversacion() {
}

void TestConversacion::testSerializacionDeDatosUnaConversacion() {
	Usuario* user = new Usuario("Pepe", "foto", "1568017070");
	Usuario* user2 = new Usuario("Jose", "foto2", "156801515");

	BaseDeDatos* db = BaseDeDatos::getInstance();
	db->setUsuario(user);
	db->setUsuario(user2);

	vector<Usuario*> usuarios;
	usuarios.push_back(user);
	usuarios.push_back(user2);

	vector<Mensaje*> mensajes;
	mensajes.push_back(new Mensaje("cuerpo mensaje 1", "asdasd", "fecha1"));
	mensajes.push_back(new Mensaje("cuerpo mensaje 2", "asdasd2", "fecha2"));

	Conversacion* conversacion = new Conversacion(usuarios, mensajes);
	string conversacionSerializada = conversacion->serializar();

	Conversacion* conv2 = new Conversacion(conversacionSerializada);
	vector<Mensaje*> mensajesDeserealizados = conv2->getMensajes();

	vector<Usuario*> usuariosDeserealizados = conv2->getUsuarios();

	CPPUNIT_ASSERT(
			mensajes[0]->getCuerpo() == mensajesDeserealizados[0]->getCuerpo());
	CPPUNIT_ASSERT(
			mensajes[1]->getCuerpo() == mensajesDeserealizados[1]->getCuerpo());
	CPPUNIT_ASSERT(
			mensajes[0]->getIdUsuarioEmisor()
					== mensajesDeserealizados[0]->getIdUsuarioEmisor());
	CPPUNIT_ASSERT(
			mensajes[1]->getIdUsuarioEmisor()
					== mensajesDeserealizados[1]->getIdUsuarioEmisor());
	CPPUNIT_ASSERT(
			mensajes[0]->getFecha() == mensajesDeserealizados[0]->getFecha());
	CPPUNIT_ASSERT(
			mensajes[1]->getFecha() == mensajesDeserealizados[1]->getFecha());

	CPPUNIT_ASSERT(user->getNombre() == usuariosDeserealizados[0]->getNombre());
	CPPUNIT_ASSERT(user->getId() == usuariosDeserealizados[0]->getId());

	CPPUNIT_ASSERT(
			user2->getNombre() == usuariosDeserealizados[1]->getNombre());
	CPPUNIT_ASSERT(user2->getId() == usuariosDeserealizados[1]->getId());

	delete user;
	delete user2;
	delete conversacion;
	delete conv2;

}

void TestConversacion::testGuardarConversacion() {
	Usuario* usuario1 = new Usuario("Juan", "123", "pass");
	Usuario* usuario2 = new Usuario("Jose", "1234", "pass");
	vector<Usuario*> usuarios;
	usuarios.push_back(usuario1);
	usuarios.push_back(usuario2);

	Mensaje* mensaje = new Mensaje("cuerpo", "123", "fecha");
	vector<Mensaje*> mensajes;
	mensajes.push_back(mensaje);

	Conversacion* conv = new Conversacion(usuarios, mensajes);
	conv->agregarMensaje(mensaje);
	conv->persistir();

	Conversacion* convBaseDatos = Conversacion::obtener(conv->getId());
	CPPUNIT_ASSERT(conv->getId() == convBaseDatos->getId());

	Conversacion::eliminar(conv->getId());
	convBaseDatos = Conversacion::obtener(conv->getId());
	CPPUNIT_ASSERT(keyIdConversacionNoEncontrada == convBaseDatos->getId());

	delete usuario1;
	delete usuario2;
	delete mensaje;
	delete conv;
}
