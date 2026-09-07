package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.junit.Before;
import org.junit.Test;

import cfml.dictionary.DictionaryManager;
import cfml.dictionary.SyntaxDictionary;
import cfml.dictionary.preferences.DictionaryPreferences;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.reporting.IErrorReporter;
import net.htmlparser.jericho.StartTag;

public class TestCFMLParserPublicAPI {
	
	private CFMLParser parser;
	
	@Before
	public void setUp() {
		DictionaryManager.initDictionaries(new DictionaryPreferences());
		parser = new CFMLParser();
	}
	
	@Test
	public void testConstructorWithDictionariesLoadsDictionary() {
		CFMLParser withDict = new CFMLParser("", "ColdFusion9");
		assertNotNull(withDict.getDictionary());
		assertTrue(withDict.getDictionary().tagExists("cfset") || withDict.getDictionary().tagExists("cfquery"));
	}
	
	@Test
	public void testSetDictionarySwapsDictionary() {
		SyntaxDictionary original = parser.getDictionary();
		assertNotNull(original);
		SyntaxDictionary replacement = DictionaryManager.getDictionary(DictionaryManager.CFDIC_KEY);
		assertNotNull(replacement);
		parser.setDictionary(replacement);
		assertSame(replacement, parser.getDictionary());
	}
	
	@Test
	public void testAddDictionaryStubDoesNotThrow() {
		parser.addDictionary("ignored");
	}
	
	@Test
	public void testAddCFMLSourceStringAndGetTags() {
		String path = "inline.cfm";
		String source = "<cfset x=1>";
		parser.addCFMLSource(path, source);
		CFMLSource cfmlSource = parser.getCFMLSource(path);
		assertNotNull(cfmlSource);
		List<StartTag> tags = cfmlSource.getAllCFMLTags();
		assertFalse(tags.isEmpty());
		assertEquals("cfset", tags.get(0).getName());
	}
	
	@Test
	public void testAddCFMLSourceFile() throws Exception {
		File temp = File.createTempFile("cfparser-api-", ".cfm");
		temp.deleteOnExit();
		Files.write(temp.toPath(), "<cfset y=2>".getBytes(StandardCharsets.UTF_8));
		CFMLSource added = parser.addCFMLSource(temp);
		assertNotNull(added);
		CFMLSource loaded = parser.getCFMLSource(temp.getPath());
		assertNotNull(loaded);
		assertFalse(loaded.getAllCFMLTags().isEmpty());
	}
	
	@Test
	public void testParseCFMLExpression() throws Exception {
		ANTLRErrorListener listener = new CollectingErrorReporter();
		CFExpression expr = parser.parseCFMLExpression("1+1", listener);
		assertNotNull(expr);
	}
	
	@Test
	public void testCreateTokenStream() throws Exception {
		CommonTokenStream tokens = parser.createTokenStream("x = 1;");
		assertNotNull(tokens);
		tokens.fill();
		assertTrue(tokens.size() > 0);
	}
	
	@Test
	public void testMessagesAndHadFatal() {
		assertFalse(parser.hadFatal());
		parser.addMessage(new ParseMessage(1, 0, 1, "x", "info"));
		assertEquals(1, parser.getMessages().size());
		assertFalse(parser.hadFatal());
		
		parser.addMessage(new ParseError(2, 0, 1, "y", "fatal-error", true));
		assertTrue(parser.hadFatal());
		assertEquals(2, parser.getMessages().size());
		
		ArrayList<ParseMessage> batch = new ArrayList<ParseMessage>();
		batch.add(new ParseMessage(3, 0, 1, "z", "batch-msg"));
		parser.addMessages(batch);
		assertEquals(3, parser.getMessages().size());
		
		String printed = parser.printMessages();
		assertNotNull(printed);
		assertTrue(printed.contains("info"));
		assertTrue(printed.contains("fatal-error"));
		assertTrue(printed.contains("batch-msg"));
	}
	
	@Test
	public void testGetCacheDebugInfoNonNull() {
		parser.addCFMLSource("cache.cfm", "<cfset a=1>");
		String info = parser.getCacheDebugInfo();
		assertNotNull(info);
	}
	
	@Test
	public void testParseAndParseElementsSmoke() {
		CFMLSource source = parser.addCFMLSource("parse-smoke.cfm", "<cfset a=1><cfquery name=\"q\">x</cfquery>");
		parser.parseElements(source);
		parser.parse();
		assertFalse(parser.getCFMLTags().isEmpty());
		assertFalse(parser.getAllTags().isEmpty());
	}
	
	@Test
	public void testSetErrorReporterAndReset() throws Exception {
		CollectingErrorReporter reporter = new CollectingErrorReporter();
		parser.setErrorReporter(reporter);
		parser.parseScript("var x = ;;;}");
		assertFalse(reporter.messages.isEmpty());
		parser.reset();
		CFScriptStatement clean = parser.parseScript("x = 1;");
		assertNotNull(clean);
	}
	
	@Test
	public void testClearDFAAfterParseScriptStillSucceeds() throws Exception {
		assertNotNull(parser.parseScript("x = 1;"));
		assertNotNull(parser.parseScript("y = 2;"));
		parser.clearDFA();
		assertNotNull(parser.parseScript("z = 3;"));
	}
	
	private static class CollectingErrorReporter implements IErrorReporter {
		final List<String> messages = new ArrayList<String>();
		
		@Override
		public void reportError(String error) {
			messages.add(error);
		}
		
		@Override
		public void reportError(RecognitionException re) {
			messages.add(re.getMessage());
		}
		
		@Override
		public void reportError(String[] tokenNames, RecognitionException e) {
			messages.add(e.getMessage());
		}
		
		@Override
		public void reportError(IntStream input, RecognitionException re, org.antlr.runtime.BitSet follow) {
			messages.add(re.getMessage());
		}
		
		@Override
		public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts,
				ATNConfigSet configs) {
		}
		
		@Override
		public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts,
				ATNConfigSet configs) {
		}
		
		@Override
		public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
				ATNConfigSet configs) {
		}
		
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
				RecognitionException e) {
			messages.add(msg);
		}
	}
}
