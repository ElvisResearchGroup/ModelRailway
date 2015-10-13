package modelrailway.testing;

import static org.junit.Assert.*;
import modelrailway.core.Route;

import org.junit.Test;
import org.junit.Assert.*;


public class TestRouteObject {
	@Test public void testRoute0(){
		Route rt = new Route(true,1,2,3,4,5);
		Integer i = rt.firstSection();
		
		assertTrue(rt.nextSection(i) == 2);
		assertTrue(rt.nextSection(2) == 3);
		assertTrue(rt.nextSection(3) == 4);
		assertTrue(rt.nextSection(4) == 5);
		assertTrue(rt.nextSection(5) == 1);
	}
	@Test public void testRoute1(){
		Route rt = new Route(true,1,2,3,4,5);
		Integer i = rt.firstSection();
		
		assertTrue(rt.prevSection(i) == 5);
		assertTrue(rt.prevSection(5) == 4);
		assertTrue(rt.prevSection(4) == 3);
		assertTrue(rt.prevSection(3) == 2);
		assertTrue(rt.prevSection(2) == 1);
	}
	
	@Test public void testRoute2(){
		Route rt = new Route(false, 1,2,3,4,5);
		
		Integer i = rt.firstSection();
		assertTrue(rt.nextSection(1) == 2);
		assertTrue(rt.nextSection(2) == 3);
		assertTrue(rt.nextSection(3) == 4);
		assertTrue(rt.nextSection(4) == 5);
		assertTrue(rt.nextSection(5) == null);
		
	}
	@Test public void testRoute3(){
		Route rt = new Route(false, 1,2,3,4,5);
		
		Integer i = rt.lastSection();
		assertTrue(rt.prevSection(i) == 4);
		assertTrue(rt.prevSection(4) == 3);
		assertTrue(rt.prevSection(3) == 2);
		assertTrue(rt.prevSection(2) == 1);
		assertTrue(rt.prevSection(1) == null);
		
	}

}
