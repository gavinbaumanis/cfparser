package cfml.dictionary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cfml.dictionary.preferences.DictionaryPreferenceConstants;
import cfml.dictionary.preferences.DictionaryPreferences;

public class TestDictionaryManager {
	
	DictionaryPreferences fPrefs;
	
	@Before
	public void setUp() throws Exception {
		fPrefs = new DictionaryPreferences();
		DictionaryManager.initDictionaries(fPrefs);
	}
	
	@Test
	public void testGetConfiguredDictionaries() {
		String[][] fun = DictionaryManager.getConfiguredDictionaries();
		assertNotNull(fun);
	}
	
	@Test
	public void testGetDictionary() {
		SyntaxDictionary fun = DictionaryManager.getDictionary(DictionaryPreferenceConstants.CFDIC_KEY);
		assertNotNull(fun);
		fun.getAllTags();
	}
	
	@Test
	public void testGetDictionaryByVersion() {
		SyntaxDictionary fun = DictionaryManager.getDictionaryByVersion(fPrefs.getCFDictionary());
		assertNotNull(fun);
		assertTrue(fun.tagExists("cfset") || fun.tagExists("cfquery"));
	}
	
	@Test
	public void testGetFirstVersion() {
		String first = DictionaryManager.getFirstVersion(DictionaryManager.CFDIC_KEY);
		assertNotNull(first);
		assertFalse(first.trim().isEmpty());
	}
	
	@Test
	public void testAddDictionaryAndGetDictionaries() {
		String key = "test-custom-dict-" + System.nanoTime();
		SyntaxDictionary dict = DictionaryManager.getDictionary(DictionaryManager.CFDIC_KEY);
		assertNotNull(dict);
		DictionaryManager.addDictionary(key, dict);
		try {
			assertSame(dict, DictionaryManager.getDictionaries().get(key));
		} finally {
			DictionaryManager.getDictionaries().remove(key);
		}
	}
	
	@Test
	public void testDictionaryCacheRoundTrip() {
		Map originalCache = DictionaryManager.getDictionariesCache();
		Map workingCache = new HashMap(originalCache);
		DictionaryManager.setDictionariesCache(workingCache);
		String key = "test-cache-dict-" + System.nanoTime();
		SyntaxDictionary dict = DictionaryManager.getDictionary(DictionaryManager.CFDIC_KEY);
		assertNotNull(dict);
		try {
			DictionaryManager.addDictionaryToCache(key, dict);
			assertSame(dict, DictionaryManager.getDictionariesCache().get(key));
		} finally {
			DictionaryManager.setDictionariesCache(originalCache);
		}
	}
	
	@Test
	public void testExternalDictionaryLocation() {
		DictionaryPreferences dprefs = new DictionaryPreferences();
		dprefs.setDictionaryDir("src/test/resources/dictionary");
		dprefs.setCFDictionary("awesomedic");
		DictionaryManager.initDictionaries(dprefs);
		String[][] fun = DictionaryManager.getConfiguredDictionaries();
		assertNotNull(fun);
		// getConfiguredDictionaries() alone is satisfied by the built-in config, so assert that the
		// dictionary named by the external preferences is the one that actually got loaded.
		assertNotNull("external dictionary 'awesomedic' was not loaded",
				DictionaryManager.getDictionaryByVersion("awesomedic"));
	}

	/**
	 * The dictionaries are eagerly loaded from the built-in defaults during class initialization, so
	 * the initialized flag is already set before any test runs. Supplying external preferences must
	 * still reload rather than short-circuit on that flag.
	 */
	@Test
	public void testExternalDictionaryLoadsAfterDefaultInitialization() {
		DictionaryManager.initDictionaries();

		DictionaryPreferences dprefs = new DictionaryPreferences();
		dprefs.setDictionaryDir("src/test/resources/dictionary");
		dprefs.setCFDictionary("awesomedic");
		DictionaryManager.initDictionaries(dprefs);

		assertNotNull("external dictionary was ignored because dictionaries were already initialized",
				DictionaryManager.getDictionaryByVersion("awesomedic"));
	}
	
	@Test
	public void testGetDicionaryByURL() {
		DictionaryPreferences dprefs = new DictionaryPreferences();
		dprefs.setDictionaryDir("src/test/resources/dictionary");
		dprefs.setCFDictionary("awesomedic");
		DictionaryManager.initDictionaries(dprefs);
		String[][] fun = DictionaryManager.getConfiguredDictionaries();
		assertNotNull(fun);
	}
	
}
