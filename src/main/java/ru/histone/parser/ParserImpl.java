/**
 *    Copyright 2012 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.tokenizer.Token;
import ru.histone.tokenizer.TokenType;
import ru.histone.tokenizer.Tokenizer;
import ru.histone.utils.GsonUtils;
import ru.histone.utils.StringEscapeUtils;
import ru.histone.utils.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Parser implementation
 */
public class ParserImpl {
	private final static Logger log = LoggerFactory.getLogger(ParserImpl.class);

	private Tokenizer tokenizer;

    /**
     * Constructs parser with {@link ru.histone.tokenizer.Tokenizer} dependency
     *
     * @param tokenizer tokenizer factory
     */
    public ParserImpl(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
	}

    /**
     * Parse input sequence into AST
     *
     * @return JSON representation of AST
     * @throws ParserException in case of parse error
     */
	public JsonArray parseTemplate() throws ParserException {
		if(tokenizer == null){
			throw new ParserException("Initialize tokenizer before parsing template");
		}
		return parse();
	}

	private JsonArray parse(TokenType... breakOn) throws ParserException {
		log.trace("parse(TokenType): breakOn={}", new Object[] { breakOn });
		JsonArray tree = new JsonArray();

		while (true) {
			Token token = tokenizer.next();

			if (token.getType() == TokenType.T_COMMENT_START) {
				log.trace("parse(TokenType): dive into comment block");
				while (!tokenizer.isNext(TokenType.T_COMMENT_END) && !tokenizer.isNext(TokenType.T_EOF)) {
					tokenizer.next();
				}
				if (tokenizer.next(TokenType.T_COMMENT_END) == null) {
					throw expectedFound("*}}", tokenizer.next());
				}
				log.trace("parse(TokenType): comment block finished");
			} else if (token.getType() == TokenType.T_LITERAL_START) {
				log.trace("parse(TokenType): dive into literal block");
				StringBuilder sb = new StringBuilder();
				while (!tokenizer.isNext(TokenType.T_LITERAL_END) && !tokenizer.isNext(TokenType.T_EOF)) {
					sb.append(tokenizer.next().getContent());
				}
				if (tokenizer.next(TokenType.T_LITERAL_END) == null) {
					throw expectedFound("%}}", tokenizer.next());
				}
				tree.add(new JsonPrimitive(sb.toString()));
				log.trace("parse(TokenType): literal block finished");
			} else if (token.getType() == TokenType.T_BLOCK_START) {
				log.trace("parse(TokenType): dive into block");
				boolean foundBreakOnToken = false;
				for (TokenType breakOnToken : breakOn) {
					if (tokenizer.isNext(breakOnToken)) {
						foundBreakOnToken = true;
						log.trace("parse(TokenType): breakOnToken={}", new Object[] { breakOnToken });
						break;
					}
				}

				if (foundBreakOnToken) {
					break;
				} else {
					if (tokenizer.next(TokenType.T_BLOCK_END) != null) {
						continue;// skip empty block
					}

					JsonElement block = parseBlock();
					if (block != null) {
						tree.add(block);
					}
				}
				log.trace("parse(TokenType): block finished");
			} else if (token.getType() == TokenType.T_BLOCK_END) {
				// do nothing
			} else if (token.getType() == TokenType.T_FRAGMENT) {
				tree.add(new JsonPrimitive(token.getContent()));
			} else if (token.getType() == TokenType.T_EOF) {
				// end of input reached, exit while loop
				break;
			} else {
				throw expectedFound("{{*, {{, }}, fragment, EOF", tokenizer.next());
			}
		}

		log.trace("parse(TokenType): result={}", new Object[] { tree });

		return tree;
	}

	private JsonElement parseBlock() throws ParserException {
		JsonElement tree = null;

		if (tokenizer.isNext(TokenType.EXPR_VAR)) {
			tree = parseVar();
		} else if (tokenizer.isNext(TokenType.EXPR_MACRO)) {
			tree = parseMacro();
		} else if (tokenizer.isNext(TokenType.EXPR_FOR)) {
			tree = parseFor();
		} else if (tokenizer.isNext(TokenType.EXPR_IF)) {
			tree = parseIf();
		} else if (tokenizer.isNext(TokenType.EXPR_CALL)) {
			tree = parseCall();
		} else if (tokenizer.isNext(TokenType.EXPRT_IMPORT)) {
            tree = parseImport();
        } else if (tokenizer.isNext(TokenType.EXPR_DIV)) {
		} else {
			tree = parseExpression();

			if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
				throw expectedFound("}}", tokenizer.next());
			}
		}

		return tree;
	}

    private JsonElement parseImport() throws ParserException {
        Token blockToken = tokenizer.next(TokenType.EXPRT_IMPORT);
        Token pathToken = tokenizer.next(TokenType.EXPR_STRING);
        if(pathToken == null) {
            throw expectedFound("string", tokenizer.next());
        }
        if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
            throw expectedFound("}}", tokenizer.next());
        }
        String path = unescapeString(StringUtils.stripSuroundings(pathToken.getContent(), "'\""));
        return AstNodeFactory.createNode(AstNodeType.IMPORT, path);
    }

    private JsonElement parseCall() throws ParserException {
		log.trace("parseCall(): >>>");
		Token blockToken = tokenizer.next(TokenType.EXPR_CALL);

		Token nameToken = tokenizer.next(TokenType.EXPR_IDENT);
		if (nameToken == null) {
			throw expectedFound("identifier", tokenizer.next());
		}
		JsonElement name = new JsonPrimitive(nameToken.getContent());

		JsonArray args = new JsonArray();
		if (tokenizer.next(TokenType.EXPR_LPAREN) != null) {
			if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
				while (true) {
					JsonElement expr = parseExpression();
					if (expr != null) {
						args.add(expr);
					}
					if (tokenizer.next(TokenType.EXPR_COMMA) == null) {
						break;
					}
				}
				if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
					throw expectedFound(")", tokenizer.next());
				}
			}
		}

		if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
			throw expectedFound("}}", tokenizer.next());
		}

		JsonElement callBlock = AstNodeFactory.createNode(AstNodeType.STATEMENTS, parse(TokenType.EXPR_DIV));
		args.add(callBlock);

		if ((tokenizer.next(TokenType.EXPR_DIV) == null) || (tokenizer.next(TokenType.EXPR_CALL) == null) || (tokenizer.next(TokenType.T_BLOCK_END) == null)) {
			throw expectedFound("{{/call}}", tokenizer.next());
		}

		JsonElement result = AstNodeFactory.createNode(AstNodeType.CALL, null, name, args);
		// tree.setPos(blockToken.getPos());

		log.trace("parseCall(): <<<");
		return result;
	}

	private JsonElement parseVar() throws ParserException {
		log.trace("parseVar(): >>>");

		// skip 'var' token
		Token blockToken = tokenizer.next(TokenType.EXPR_VAR);

		Token nameToken = tokenizer.next(TokenType.EXPR_IDENT);
		if (nameToken == null) {
			throw expectedFound("identifier", tokenizer.next());
		}
		JsonPrimitive name = new JsonPrimitive(nameToken.getContent());

		JsonElement expression;
		if (tokenizer.next(TokenType.EXPR_ASSIGN) == null) {
			if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
				throw expectedFound("}}", tokenizer.next());
			}

			expression = AstNodeFactory.createNode(AstNodeType.STATEMENTS, parse(TokenType.EXPR_DIV));

			if ((tokenizer.next(TokenType.EXPR_DIV) == null) || (tokenizer.next(TokenType.EXPR_VAR) == null) || (tokenizer.next(TokenType.T_BLOCK_END) == null)) {
				throw expectedFound("{{/var}}", tokenizer.next());
			}
		} else {
			expression = parseExpression();

			if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
				throw expectedFound("}}", tokenizer.next());

			}
		}

		JsonElement result = AstNodeFactory.createNode(AstNodeType.VAR, name, expression);
		// tree.setPos(blockToken.getPos());

		log.trace("parseVar(): <<<");

		return result;
	}

    private JsonElement parseMacro() throws ParserException {
		log.trace("parseMacro(): >>>");

		// skip 'macro' token
		Token blockToken = tokenizer.next(TokenType.EXPR_MACRO);

		Token nameToken = tokenizer.next(TokenType.EXPR_IDENT);
		if (nameToken == null) {
			throw expectedFound("identifier", tokenizer.next());
		}
		JsonPrimitive name = new JsonPrimitive(nameToken.getContent());

		JsonArray args = new JsonArray();
		if (tokenizer.next(TokenType.EXPR_LPAREN) != null) {
			if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
				while (true) {
					if (!tokenizer.isNext(TokenType.EXPR_IDENT)) {
						throw expectedFound("identifier", tokenizer.next());
					}

					args.add(new JsonPrimitive(tokenizer.next(TokenType.EXPR_IDENT).getContent()));

					if (tokenizer.next(TokenType.EXPR_COMMA) == null) {
						break;
					}
				}
				if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
					throw expectedFound(")", tokenizer.next());
				}

			}
		}

		if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
			throw expectedFound("}}", tokenizer.next());
		}

		JsonArray macroBlock = parse(TokenType.EXPR_DIV);

		if ((tokenizer.next(TokenType.EXPR_DIV) == null) || (tokenizer.next(TokenType.EXPR_MACRO) == null) || (tokenizer.next(TokenType.T_BLOCK_END) == null)) {
			throw expectedFound("{{/macro}}", tokenizer.next());
		}

		JsonElement result = AstNodeFactory.createNode(AstNodeType.MACRO, name, args, macroBlock);
		// tree.setPos(blockToken.getPos());

		log.trace("parseMacro(): <<<");
		return result;
	}

	private JsonElement parseFor() throws ParserException {
		log.trace("parseFor(): >>>");
		// skip 'for' token
		Token blockToken = tokenizer.next(TokenType.EXPR_FOR);

		Token valueToken = tokenizer.next(TokenType.EXPR_IDENT);
		if (valueToken == null) {
			throw expectedFound("identifier", tokenizer.next());
		}

		Token keyToken = null;
		if (tokenizer.next(TokenType.EXPR_COLON) != null) {
			keyToken = valueToken;
			valueToken = tokenizer.next(TokenType.EXPR_IDENT);
			if (valueToken == null) {
				throw expectedFound("identifier", tokenizer.next());
			}
		}

		if (tokenizer.next(TokenType.EXPR_IN) == null) {
			throw expectedFound("in", tokenizer.next());
		}

		JsonElement expression = parseExpression();

		if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
			throw expectedFound("}}", tokenizer.next());
		}

		JsonArray forBlock = parse(TokenType.EXPR_DIV, TokenType.EXPR_ELSE);

		JsonArray elseBlock = null;
		if (tokenizer.next(TokenType.EXPR_ELSE) != null) {
			if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
				throw expectedFound("}}", tokenizer.next());
			}

			elseBlock = parse(TokenType.EXPR_DIV);
		}

		if ((tokenizer.next(TokenType.EXPR_DIV) == null) || (tokenizer.next(TokenType.EXPR_FOR) == null) || (tokenizer.next(TokenType.T_BLOCK_END) == null)) {
			throw expectedFound("{{/for}}", tokenizer.next());
		}

		JsonArray iterator = new JsonArray();
		iterator.add(new JsonPrimitive(valueToken.getContent()));
		if (keyToken != null) {
			iterator.add(new JsonPrimitive(keyToken.getContent()));
		}

		JsonArray statements = new JsonArray();
		statements.add(forBlock);
		if (elseBlock != null) {
			statements.add(elseBlock);
		}

		JsonElement result = AstNodeFactory.createNode(AstNodeType.FOR, iterator, expression, statements);
		// tree.setPos(blockToken.getPos());

		log.trace("parseFor(): <<<");

		return result;
	}

	private JsonElement parseIf() throws ParserException {
		log.trace("parseIf(): >>>");
		// skip 'var' token
		Token blockToken = tokenizer.next(TokenType.EXPR_IF);

		JsonArray conditions = new JsonArray();

		while (true) {
			if (tokenizer.isNext(TokenType.T_BLOCK_END)) {
				throw expectedFound("expression", tokenizer.next());
			}

			JsonElement expression = parseExpression();

            if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
                throw expectedFound("}}", tokenizer.next());
            }

			JsonArray children = parse(TokenType.EXPR_DIV, TokenType.EXPR_ELSEIF, TokenType.EXPR_ELSE);

			JsonArray condition = new JsonArray();
			// condition.add(AstNodeFactory.createNode(AstNodeType.TRUE));
			condition.add(expression);
			condition.add(children);

			conditions.add(condition);

			if (tokenizer.next(TokenType.EXPR_ELSEIF) == null) {
				break;
			}
		}

		if (tokenizer.next(TokenType.EXPR_ELSE) != null) {
            if (tokenizer.next(TokenType.T_BLOCK_END) == null) {
                throw expectedFound("}}", tokenizer.next());
            }

			JsonArray children = parse(TokenType.EXPR_DIV);

			JsonArray condition = new JsonArray();
			condition.add(AstNodeFactory.createNode(AstNodeType.TRUE));
			condition.add(children);

			conditions.add(condition);
		}

		if ((tokenizer.next(TokenType.EXPR_DIV) == null) || (tokenizer.next(TokenType.EXPR_IF) == null) || (tokenizer.next(TokenType.T_BLOCK_END) == null)) {
			throw expectedFound("{{/if}}", tokenizer.next());
		}

		JsonElement tree = AstNodeFactory.createNode(AstNodeType.IF, conditions);
		// tree.setPos(blockToken.getPos());

		log.trace("parseIf(): <<<");
		return tree;
	}

	private JsonElement parseExpression() throws ParserException {
		log.trace("parseExpression(): >>>");
		JsonElement result = parseTernaryExpression();
		log.trace("parseExpression(): result={}", result);
		log.trace("parseExpression(): <<<");
		return result;
	}

	private JsonElement parseTernaryExpression() throws ParserException {
		// log.trace("parseTernaryExpression()");

		JsonElement left = parseOrExpression();
		JsonElement thenNode, elseNode;
		while (tokenizer.next(TokenType.EXPR_QUERY) != null) {
			thenNode = parseExpression();
            if (tokenizer.next(TokenType.EXPR_COLON) == null) {
                //STE-61 we can skip part after colon
                left = AstNodeFactory.createNode(AstNodeType.TERNARY, left, thenNode);
            } else {
                elseNode = parseExpression();
                left = AstNodeFactory.createNode(AstNodeType.TERNARY, left, thenNode, elseNode);
            }
        }

        return left;
	}

	private JsonElement parseOrExpression() throws ParserException {
		// log.trace("parseOrExpression()");

		JsonElement left = parseAndExpression();
		while (tokenizer.next(TokenType.EXPR_OR) != null) {
			// int pos = left.getPos();
			left = AstNodeFactory.createNode(AstNodeType.OR, left, parseAndExpression());
			// left.setPos(pos);
		}
		return left;
	}

	private JsonElement parseAndExpression() throws ParserException {
		// log.trace("parseAndExpression()");

		JsonElement left = parseEqualityExpression();
		while (tokenizer.next(TokenType.EXPR_AND) != null) {
			// int pos = left.getPos();
			left = AstNodeFactory.createNode(AstNodeType.AND, left, parseEqualityExpression());
			// left.setPos(pos);
		}
		return left;
	}

	private JsonElement parseEqualityExpression() throws ParserException {
		// log.trace("parseEqualityExpression()");

		JsonElement left = parseRelationalExpression();
		while (tokenizer.isNext(TokenType.EXPR_IS) || tokenizer.isNext(TokenType.EXPR_NOT_EQUAL)) {
			// int pos = left.getPos();

			if (tokenizer.next(TokenType.EXPR_IS) != null) {
				left = AstNodeFactory.createNode(AstNodeType.EQUAL, left, parseRelationalExpression());
			} else if (tokenizer.next(TokenType.EXPR_NOT_EQUAL) != null) {
				left = AstNodeFactory.createNode(AstNodeType.NOT_EQUAL, left, parseRelationalExpression());
			}
			// left.setPos(pos);

		}
		return left;
	}

	private JsonElement parseRelationalExpression() throws ParserException {
		// log.trace("parseRelationalExpression()");

		JsonElement left = parseAdditiveExpression();
		while (tokenizer.isNext(TokenType.EXPR_LESS_OR_EQUAL) || tokenizer.isNext(TokenType.EXPR_LESS_THAN) || tokenizer.isNext(TokenType.EXPR_GREATER_OR_EQUAL)
				|| tokenizer.isNext(TokenType.EXPR_GREATER_THAN)) {
			// int pos = left.getPos();

			if (tokenizer.next(TokenType.EXPR_LESS_OR_EQUAL) != null) {
				left = AstNodeFactory.createNode(AstNodeType.LESS_OR_EQUAL, left, parseAdditiveExpression());
			} else if (tokenizer.next(TokenType.EXPR_LESS_THAN) != null) {
				left = AstNodeFactory.createNode(AstNodeType.LESS_THAN, left, parseAdditiveExpression());
			} else if (tokenizer.next(TokenType.EXPR_GREATER_OR_EQUAL) != null) {
				left = AstNodeFactory.createNode(AstNodeType.GREATER_OR_EQUAL, left, parseAdditiveExpression());
			} else if (tokenizer.next(TokenType.EXPR_GREATER_THAN) != null) {
				left = AstNodeFactory.createNode(AstNodeType.GREATER_THAN, left, parseAdditiveExpression());
			}
			// left.setPos(pos);

		}
		return left;
	}

	private JsonElement parseAdditiveExpression() throws ParserException {
		// log.trace("parseAdditiveExpression()");

		JsonElement left = parseMultiplicativeExpression();
		while (tokenizer.isNext(TokenType.EXPR_ADD) || tokenizer.isNext(TokenType.EXPR_SUB)) {
			// int pos = left.getPos();

			if (tokenizer.next(TokenType.EXPR_ADD) != null) {
				left = AstNodeFactory.createNode(AstNodeType.ADD, left, parseMultiplicativeExpression());
			} else if (tokenizer.next(TokenType.EXPR_SUB) != null) {
				left = AstNodeFactory.createNode(AstNodeType.SUB, left, parseMultiplicativeExpression());
			}
			// left.setPos(pos);

		}
		return left;
	}

	private JsonElement parseMultiplicativeExpression() throws ParserException {
		// log.trace("parseMultiplicativeExpression()");

		JsonElement left = parseUnaryExpression();
		while (tokenizer.isNext(TokenType.EXPR_MUL) || tokenizer.isNext(TokenType.EXPR_DIV) || tokenizer.isNext(TokenType.EXPR_MOD)) {
			// int pos = left.getPos();

			if (tokenizer.next(TokenType.EXPR_MUL) != null) {
				left = AstNodeFactory.createNode(AstNodeType.MUL, left, parseUnaryExpression());
			} else if (tokenizer.next(TokenType.EXPR_DIV) != null) {
				left = AstNodeFactory.createNode(AstNodeType.DIV, left, parseUnaryExpression());
			} else if (tokenizer.next(TokenType.EXPR_MOD) != null) {
				left = AstNodeFactory.createNode(AstNodeType.MOD, left, parseUnaryExpression());
			}
			// left.setPos(pos);

		}
		return left;
	}

	private JsonElement parseUnaryExpression() throws ParserException {
		// log.trace("parseUnaryExpression()");

		JsonElement tree;

		if ((tokenizer.next(TokenType.EXPR_NOT) != null)) {
			tree = AstNodeFactory.createNode(AstNodeType.NOT, parseUnaryExpression());
		} else {
			tokenizer.next(TokenType.EXPR_ADD);
			tree = parsePrimaryExpression();
		}

		return tree;
	}

	private JsonElement parsePrimaryExpression() throws ParserException {
		log.trace("parsePrimaryExpression(): >>>");

		JsonElement left = null;
		if ((tokenizer.next(TokenType.EXPR_SUB) != null)) {
			left = AstNodeFactory.createNode(AstNodeType.NEGATE, parseSimpleExpression());
		} else {
			left = parseSimpleExpression();
		}

		while (true) {
			if (tokenizer.next(TokenType.EXPR_DOT) != null) {
				if (!tokenizer.isNext(TokenType.EXPR_IDENT)) {
					throw expectedFound("identifier", tokenizer.next());
				}
				if (!isType(AstNodeType.SELECTOR, left)) {
					left = AstNodeFactory.createNode(AstNodeType.SELECTOR, AstNodeFactory.createArray(left));
				}
				left.getAsJsonArray().get(1).getAsJsonArray().add(new JsonPrimitive(tokenizer.next().getContent()));
			} else if (tokenizer.next(TokenType.EXPR_LBRACKET) != null) {
				if (tokenizer.next(TokenType.EXPR_RBRACKET) != null) {
					throw expectedFound("expression", "[]");
				}
				if (!isType(AstNodeType.SELECTOR, left)) {
					left = AstNodeFactory.createNode(AstNodeType.SELECTOR, AstNodeFactory.createArray(left));
				}
				left.getAsJsonArray().get(1).getAsJsonArray().add(parseExpression());

				if (tokenizer.next(TokenType.EXPR_RBRACKET) == null) {
					throw expectedFound("]", tokenizer.next());
				}
			} else if (tokenizer.next(TokenType.EXPR_LPAREN) != null) {
                if(!isType(AstNodeType.SELECTOR, left)) {
                    throw expectedFound("}}", "(");
                }
                JsonArray leftArr = (JsonArray) left;
                JsonElement name = GsonUtils.removeLast(leftArr.get(1).getAsJsonArray());
                if (leftArr.get(1).getAsJsonArray().size() == 0) {
                    left = JsonNull.INSTANCE;
                }

				JsonArray args = new JsonArray();
				if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
					while (true) {
						args.add(parseExpression());
						if (tokenizer.next(TokenType.EXPR_COMMA) == null) {
							break;
						}
					}
					if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
						throw expectedFound(")", tokenizer.next());
					}
				}

                /*if(name.isJsonArray()) {
                    JsonArray nameArr = name.getAsJsonArray();
                    JsonElement nameFirstElement = nameArr.get(0);
                    if(nameFirstElement.isJsonPrimitive() && nameFirstElement.getAsJsonPrimitive().getAsNumber().equals(AstNodeType.STRING)) {
                        name = nameArr.get(1);
                    }
                }*/

				left = AstNodeFactory.createNode(AstNodeType.CALL, left, name, (args.size() == 0) ? JsonNull.INSTANCE : args);
			} else {
				break;
			}
		}

		log.trace("parsePrimaryExpression(): <<<");

		return left;
	}

	private boolean isType(int type, JsonElement elem) {
		JsonArray leftArray = (JsonArray) elem;
		int elemType = leftArray.get(0).getAsInt();
		return type == elemType;
	}

	private JsonElement parseSimpleExpression() throws ParserException {
		log.trace("parseSimpleExpression(): >>>");

		JsonElement tree = null;

		if (tokenizer.next(TokenType.EXPR_NULL) != null) {
			tree = AstNodeFactory.createNode(AstNodeType.NULL);
		} else if ((tokenizer.next(TokenType.EXPR_TRUE) != null)) {
			tree = AstNodeFactory.createNode(AstNodeType.TRUE);
		} else if ((tokenizer.next(TokenType.EXPR_FALSE) != null)) {
			tree = AstNodeFactory.createNode(AstNodeType.FALSE);
		} else if (tokenizer.isNext(TokenType.EXPR_INTEGER)) {
			Token token = tokenizer.next();
			BigInteger val = new BigInteger(token.getContent());
			tree = AstNodeFactory.createNode(AstNodeType.INT, val);
		} else if (tokenizer.isNext(TokenType.EXPR_DOUBLE)) {
			Token token = tokenizer.next();
			BigDecimal val = new BigDecimal(token.getContent());
			tree = AstNodeFactory.createNode(AstNodeType.DOUBLE, val);
		} else if (tokenizer.isNext(TokenType.EXPR_STRING)) {
			Token token = tokenizer.next();
			String val = unescapeString(StringUtils.stripSuroundings(token.getContent(), "'\""));
			tree = AstNodeFactory.createNode(AstNodeType.STRING, val);
        } else if (tokenizer.next(TokenType.EXPR_LBRACKET) != null) {
            tree = parseMap(tokenizer);
//		} else if (tokenizer.next(TokenType.EXPR_ARRAY) != null) {
//			JsonArray args = new JsonArray();
//			if (tokenizer.next(TokenType.EXPR_LPAREN) == null) {
//				throw expectedFound("(", tokenizer.next());
//			}
//			if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
//				while (true) {
//					args.add(parseExpression());
//					if (tokenizer.next(TokenType.EXPR_COMMA) == null) {
//						break;
//					}
//				}
//				if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
//					throw expectedFound(")", tokenizer.next());
//				}
//			}
//			tree = AstNodeFactory.createNode(AstNodeType.ARRAY, args);
//		} else if (tokenizer.next(TokenType.EXPR_OBJECT) != null) {
//			if (tokenizer.next(TokenType.EXPR_LPAREN) == null) {
//				throw expectedFound("(", tokenizer.next());
//			}
//
//			JsonArray props = new JsonArray();
//			String key = null;
//			if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
//				while (true) {
//
//					if (tokenizer.isNext(TokenType.EXPR_IDENT) || tokenizer.isNext(TokenType.EXPR_INTEGER)) {
//						key = tokenizer.next().getContent();
//					} else if (tokenizer.isNext(TokenType.EXPR_STRING)) {
//                        key = StringUtils.stripSuroundings(tokenizer.next().getContent(), "'\"");
//					} else {
//						throw expectedFound("identifier, string, number", tokenizer.next());
//					}
//
//					if (tokenizer.next(TokenType.EXPR_COLON) == null) {
//						throw expectedFound(":", tokenizer.next());
//					}
//
//					JsonArray prop = new JsonArray();
//					prop.add(new JsonPrimitive(key));
//					prop.add(parseExpression());
//					props.add(prop);
//
//					if (tokenizer.next(TokenType.EXPR_COMMA) == null) {
//						break;
//					}
//				}
//				if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
//					throw expectedFound(")", tokenizer.next());
//				}
//			}
//
//			tree = AstNodeFactory.createNode(AstNodeType.OBJECT, props);
		} else if (tokenizer.isNext(TokenType.EXPR_IDENT) || tokenizer.isNext(TokenType.EXPR_THIS) || tokenizer.isNext(TokenType.EXPR_SELF) || tokenizer.isNext(TokenType.EXPR_GLOBAL)) {
			Token token = tokenizer.next();
			JsonArray val = new JsonArray();
			val.add(new JsonPrimitive(token.getContent()));
			tree = AstNodeFactory.createNode(AstNodeType.SELECTOR, val);
		} else if (tokenizer.next(TokenType.EXPR_LPAREN) != null) {
			if (tokenizer.isNext(TokenType.EXPR_RPAREN)) {
				throw expectedFound("expression", "()");
			}
			tree = parseExpression();
			if (tokenizer.next(TokenType.EXPR_RPAREN) == null) {
				throw expectedFound(")", tokenizer.next());
			}
		} else {
			throw expectedFound("expression", tokenizer.next());
		}

		log.trace("parseSimpleExpression(): <<<");

		return tree;
	}

    private JsonElement parseMap(Tokenizer tokenizer) throws ParserException {

        JsonArray items = new JsonArray();
        JsonElement key, value;

        if (!tokenizer.isNext(TokenType.EXPR_RBRACKET)) {
            while (true) {

                key = null;
                value = parseExpression();

                if (tokenizer.next(TokenType.EXPR_COLON) != null) {

                    int value0 = value.getAsJsonArray().get(0).getAsInt();
                    if (value0 != AstNodeType.STRING &&
                            value0 != AstNodeType.INT && (
                            value0 != AstNodeType.SELECTOR ||
                                    value.getAsJsonArray().get(1).getAsJsonArray().size() != 1
                    )) {
                        throw expectedFound("identifier, string, number", "node of type '" + value0 + "'");
                    }

                    if (value0 != AstNodeType.SELECTOR) {
                        key = value.getAsJsonArray().get(1);
                    } else {
                        key = value.getAsJsonArray().get(1).getAsJsonArray().get(0);
                    }

                    value = parseExpression();
                }

                if (key != null && key.getAsJsonPrimitive().isNumber()) {
                    key = new JsonPrimitive(key.getAsString());
                }

                JsonArray elem = AstNodeFactory.createArray(key, value);
                items.add(elem);
                if (tokenizer.next(TokenType.EXPR_COMMA) == null) {
                    break;
                }
            }
        }
        if (tokenizer.next(TokenType.EXPR_RBRACKET) == null) {
            throw expectedFound("]", tokenizer.next());
        }
        return AstNodeFactory.createNode(AstNodeType.MAP, items);
    }

    private static String unescapeString(String val) {
		return StringEscapeUtils.unescapeJavaScript(val);
	}

	// public static String escapeString(String val) {
	// return StringEscapeUtils.escapeJavaScript(val);
	// }

	private ParserException expectedFound(String expected, Token found) {
		return expectedFound(expected, found.getContent());
	}

	private ParserException expectedFound(String expected, String found) {
		return new ParserException(tokenizer.getLineNumber(), expected, found);
	}

	// private ParserException unexpected(Token unexpected) {
	// return new ParserException(tokenizer.getLineNumber(),
	// tokenizer.getColumnNumber(), unexpected);
	// }

}
