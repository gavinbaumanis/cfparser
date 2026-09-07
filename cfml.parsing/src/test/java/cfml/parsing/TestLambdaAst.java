package cfml.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cfml.parsing.cfscript.CFAnonymousFunctionExpression;
import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFullVarExpression;
import cfml.parsing.cfscript.CFFunctionExpression;
import cfml.parsing.cfscript.script.CFCompDeclStatement;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

public class TestLambdaAst {

	private CFMLParser parser;

	@Before
	public void setUp() {
		parser = new CFMLParser();
	}

	private CFScriptStatement parse(String script) {
		try {
			CFScriptStatement stmt = parser.parseScript(script);
			if (parser.getMessages().size() > 0) {
				fail("parse messages: " + parser.getMessages());
			}
			return stmt;
		} catch (Exception e) {
			fail(e.getMessage());
			return null;
		}
	}

	@Test
	public void lambdaAssignedBuildsAnonymousFunctionExpression() {
		CFScriptStatement script = parse("isOdd = (numeric n) => { return n; };");
		assertTrue(script instanceof CFExpressionStatement);
		CFExpression expr = ((CFExpressionStatement) script).getExpression();
		assertTrue(expr instanceof CFAssignmentExpression);
		CFExpression right = ((CFAssignmentExpression) expr).getRight();
		assertTrue(right instanceof CFAnonymousFunctionExpression);
		CFFuncDeclStatement decl = ((CFAnonymousFunctionExpression) right).getFunctionDeclaration();
		assertNotNull(decl);
		assertEquals(1, decl.getFormals().size());
		assertNotNull(decl.getBody());
	}

	@Test
	public void blockArrowAsStructValue() {
		String script = "component { function test() {"
				+ " rptdetails = { 'data' = seq.Map(rows, (row) => { return row.id; }) };"
				+ "} }";
		CFScriptStatement root = parse(script);
		assertTrue(root instanceof CFCompDeclStatement);
	}

	@Test
	public void mapArgLambdaIsAnonymousFunctionExpression() {
		CFScriptStatement script = parse("data = seq.Map(rows, (row) => { return row.id; });");
		CFExpression assign = ((CFExpressionStatement) script).getExpression();
		CFExpression right = ((CFAssignmentExpression) assign).getRight();
		CFFunctionExpression mapCall = findFunctionCall(right, "map");
		assertNotNull("expected Map(...) call", mapCall);
		assertEquals(2, mapCall.getArgs().size());
		assertTrue(mapCall.getArgs().get(1) instanceof CFAnonymousFunctionExpression);
	}

	@Test
	public void trailingCommaInCallArgs() {
		parse("foo(1, 2,);");
	}

	@Test
	public void elvisMemberAccess() {
		parse("x = pat.uniqueno ?: '';");
	}

	@Test
	public void ternaryYieldingStruct() {
		parse("out = isNull(x) ? '' : { value = x, html = 'y' };");
	}

	@Test
	public void nestedQuotesInSingleQuotedInterpolation() {
		parse("s = '#DateFormat(ts,'YYYYMMDD')#';");
	}

	@Test
	public void queryExecuteDoubleHash() {
		parse("q = QueryExecute(\"SELECT '##' AS x\");");
	}

	@Test
	public void loopInsideArrow() {
		String script = "rptdata = rptdata.Filter((row) => {"
				+ " loop collection = score.subscores index = \"k\" item = \"v\" {"
				+ " if (v.score > 2) { return true; }"
				+ " }"
				+ " return false;"
				+ "});";
		parse(script);
	}

	@Test
	public void requiredBooleanAndStaticCall() {
		String script = "component {"
				+ " function declOk(required boolean flag, required string name,) { return true; }"
				+ " function staticCall() { return CompName::runStatic(); }"
				+ "}";
		CFScriptStatement stmt = parse(script);
		assertTrue(stmt instanceof CFCompDeclStatement);
	}

	private CFFunctionExpression findFunctionCall(CFExpression expr, String name) {
		if (expr == null) {
			return null;
		}
		if (expr instanceof CFFunctionExpression) {
			CFFunctionExpression fn = (CFFunctionExpression) expr;
			if (name.equalsIgnoreCase(fn.getFunctionName())) {
				return fn;
			}
			for (CFExpression arg : fn.getArgs()) {
				CFFunctionExpression found = findFunctionCall(arg, name);
				if (found != null) {
					return found;
				}
			}
		}
		if (expr instanceof CFFullVarExpression) {
			List<CFExpression> parts = ((CFFullVarExpression) expr).getExpressions();
			for (CFExpression part : parts) {
				CFFunctionExpression found = findFunctionCall(part, name);
				if (found != null) {
					return found;
				}
			}
		}
		for (CFExpression child : expr.decomposeExpression()) {
			CFFunctionExpression found = findFunctionCall(child, name);
			if (found != null) {
				return found;
			}
		}
		return null;
	}
}
