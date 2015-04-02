/*
 * TestUsuario.cpp
 *
 *  Created on: 30/3/2015
 *      Author: matias
 */

#include "TestUsuario.h"

CPPUNIT_TEST_SUITE_REGISTRATION(TestUsuario);

TestUsuario::TestUsuario() {
	// TODO Auto-generated constructor stub

}

TestUsuario::~TestUsuario() {
	// TODO Auto-generated destructor stub
}

void TestUsuario::testSerializacionDeDatosDeUnUsuario(){

	Usuario* unUsuario = new Usuario("Pepe" , "foto" , "123456789");
	string usuarioSerializado = unUsuario->serializar();

	Json::Value user;
	Json::Reader reader;

	reader.parse(usuarioSerializado, user);

	CPPUNIT_ASSERT("Pepe" == user.get(keyNombre, "").asString() );
	CPPUNIT_ASSERT("123456789" == user.get(keyTelefono, "").asString());
	CPPUNIT_ASSERT("foto" == user.get(keyFotoDePerfil, "").asString());


	delete unUsuario;

}

void TestUsuario::testLaDeserializacionDebeActualizarLosDatosDeUnUsuario(){

	Usuario* unUsuario = new Usuario("Pedro" , "foto" , "123456789");
	string nuevoTelefono = "1122334455";
	unUsuario->setTelefono(nuevoTelefono);
	string usuarioSerializado = unUsuario->serializar();
	unUsuario->deserealizar(usuarioSerializado);

	CPPUNIT_ASSERT(nuevoTelefono == unUsuario->getTelefono() );

	delete unUsuario;
}
