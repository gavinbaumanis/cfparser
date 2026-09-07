package cfml.dictionary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cfml.dictionary.preferences.DictionaryPreferences;
import cfml.dictionary.syntax.CFSyntaxDictionary;

public class TestSyntaxDictionary {
	
	private SyntaxDictionary dictionary;
	
	@Before
	public void setUp() {
		DictionaryManager.initDictionaries(new DictionaryPreferences());
		dictionary = DictionaryManager.getDictionary(DictionaryManager.CFDIC_KEY);
	}
	
	@Test
	public void testDefaultDictionaryLoaded() {
		assertNotNull(dictionary);
	}
	
	@Test
	public void testTagExistsAndGetTag() {
		assertTrue(dictionary.tagExists("cfquery") || dictionary.tagExists("cfset"));
		Tag tag = dictionary.tagExists("cfquery") ? dictionary.getTag("cfquery") : dictionary.getTag("cfset");
		assertNotNull(tag);
	}
	
	@Test
	public void testGetElementAttributes() {
		String tagName = dictionary.tagExists("cfquery") ? "cfquery" : "cfset";
		Set<Parameter> attrs = dictionary.getElementAttributes(tagName);
		assertNotNull(attrs);
		assertFalse(attrs.isEmpty());
	}
	
	@Test
	public void testFunctionExistsAndGetFunction() {
		assertTrue(dictionary.functionExists("abs"));
		assertNotNull(dictionary.getFunction("abs"));
	}
	
	@Test
	public void testGetAllTagsFunctionsAndElementsNonEmpty() {
		assertFalse(dictionary.getAllTags().isEmpty());
		assertFalse(dictionary.getAllFunctions().isEmpty());
		assertFalse(dictionary.getAllElements().isEmpty());
	}
	
	@Test
	public void testGetFunctionParamsAndHelpForAbs() {
		Set<Parameter> params = dictionary.getFunctionParams("abs");
		assertNotNull(params);
		String help = dictionary.getFunctionHelp("abs");
		assertNotNull(help);
	}
	
	@Test
	public void testGetFilteredElements() {
		Set<Object> filtered = dictionary.getFilteredElements("cfset");
		assertNotNull(filtered);
		assertFalse(filtered.isEmpty());
	}
	
	@Test
	public void testCFSyntaxDictionaryOperatorsAndKeywords() {
		assertTrue(dictionary instanceof CFSyntaxDictionary);
		CFSyntaxDictionary cfDict = (CFSyntaxDictionary) dictionary;
		assertFalse(cfDict.getOperators().isEmpty());
		assertFalse(cfDict.getScriptKeywords().isEmpty());
		assertTrue(cfDict.getOperators().contains("eq"));
		assertTrue(cfDict.getScriptKeywords().contains("if"));
	}
	
	@Test
	public void testGetDictionaryByVersion() {
		DictionaryPreferences prefs = new DictionaryPreferences();
		DictionaryManager.initDictionaries(prefs);
		SyntaxDictionary byVersion = DictionaryManager.getDictionaryByVersion(prefs.getCFDictionary());
		assertNotNull(byVersion);
		assertTrue(byVersion.tagExists("cfset") || byVersion.tagExists("cfquery"));
	}
}
