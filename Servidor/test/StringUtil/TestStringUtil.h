/*
 * TestStringUtil.h
 *
 *  Created on: 10/4/2015
 *      Author: matias
 */

#ifndef TEST_STRINGUTIL_TESTSTRINGUTIL_H_
#define TEST_STRINGUTIL_TESTSTRINGUTIL_H_

#include "../src/Utilidades/StringUtil.h"
#include "../src/constantes.h"
#include <cppunit/extensions/HelperMacros.h>

using namespace CPPUNIT_NS;
using namespace std;

class TestStringUtil: public TestFixture {
	CPPUNIT_TEST_SUITE(TestStringUtil);
	CPPUNIT_TEST(testToLowerDeberiaPasarTodoAMinuscula);
	CPPUNIT_TEST(testToUpperDeberiaPasarTodoAMayuscula);
	CPPUNIT_TEST(testStringToBoolean);
	CPPUNIT_TEST(testBooleanToString);
	CPPUNIT_TEST(testStringToInt);
	CPPUNIT_TEST(testStringToChar);
	CPPUNIT_TEST(testVectorContiene);
	CPPUNIT_TEST(testJsonValueToVector);
	CPPUNIT_TEST_SUITE_END();
public:
	TestStringUtil();
    void testToLowerDeberiaPasarTodoAMinuscula();
    void testToUpperDeberiaPasarTodoAMayuscula();
    void testStringToBoolean();
    void testBooleanToString();
    void testStringToInt();
    void testStringToChar();
    void testVectorContiene();
    void testJsonValueToVector();
	virtual ~TestStringUtil();
};

#endif /* TEST_STRINGUTIL_TESTSTRINGUTIL_H_ */
