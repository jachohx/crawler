package cn.jachohx.crawler.jdbc;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Simple adapter for PreparedStatementSetter that applies
 * a given array of arguments.
 *
 * @author Juergen Hoeller
 */
class ArgPreparedStatementSetter implements PreparedStatementSetter {

	private final Object[] args;


	/**
	 * Create a new ArgPreparedStatementSetter for the given arguments.
	 * @param args the arguments to set
	 */
	public ArgPreparedStatementSetter(Object[] args) {
		this.args = args;
	}


	public void setValues(PreparedStatement ps) throws SQLException {
		if (this.args != null) {
			for (int i = 0; i < this.args.length; i++) {
				Object arg = this.args[i];
				StatementCreatorUtils.setParameterValue(ps, i + 1, arg);
			}
		}
	}

}