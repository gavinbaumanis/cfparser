package cfml.parsing.cfscript;

import java.util.List;

import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFFunctionParameter;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

/**
 * Expression tree for anonymous function(){} and arrow ()=> bodies.
 */
public class CFAnonymousFunctionExpression extends CFExpression {
	private static final long serialVersionUID = 1L;

	private CFFuncDeclStatement funcDeclStatement;
	private boolean lambda;

	public CFAnonymousFunctionExpression(org.antlr.v4.runtime.Token _t, CFFuncDeclStatement funcDeclStatement) {
		this(_t, funcDeclStatement, false);
	}

	public CFAnonymousFunctionExpression(org.antlr.v4.runtime.Token _t, CFFuncDeclStatement funcDeclStatement,
			boolean lambda) {
		super(_t);
		this.funcDeclStatement = funcDeclStatement;
		this.lambda = lambda;
		if (funcDeclStatement != null) {
			funcDeclStatement.setParent(this);
		}
	}

	@Override
	public byte getType() {
		return CFExpression.NESTED;
	}

	@Override
	public String Decompile(int indent) {
		if (!lambda || funcDeclStatement == null) {
			return funcDeclStatement == null ? "" : funcDeclStatement.Decompile(0);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		List<CFFunctionParameter> formals = funcDeclStatement.getFormals();
		if (formals != null) {
			for (int i = 0; i < formals.size(); i++) {
				sb.append(formals.get(i));
				if (i != formals.size() - 1) {
					sb.append(", ");
				}
			}
		}
		String arrow = getToken() == null ? "=>" : getToken().getText();
		sb.append(") ").append(arrow).append(" ");
		if (funcDeclStatement.getBody() != null) {
			sb.append(funcDeclStatement.getBody().Decompile(0));
		}
		return sb.toString();
	}

	public boolean isLambda() {
		return lambda;
	}

	/**
	 * True for <code>=&gt;</code>, which Lucee evaluates as a closure over the enclosing scope, as
	 * against <code>-&gt;</code>, which does not capture it.
	 */
	public boolean isClosure() {
		return lambda && (getToken() == null || "=>".equals(getToken().getText()));
	}

	public CFFuncDeclStatement getFunctionDeclaration() {
		return funcDeclStatement;
	}

	public CFFuncDeclStatement getFuncDeclStatement() {
		return funcDeclStatement;
	}

	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression();
	}

	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(funcDeclStatement);
	}

}
