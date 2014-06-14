/*
 * Copyright 2011-2014 Tim Berglund and Steven C. Saliman
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.saliman.liquibase.delegate

import liquibase.exception.ChangeLogParseException
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * Tests for {@link CommentDelegate}  It makes sure it can be called in all
 * its various permutations.
 *
 * @author Steven C. Saliman
 */
class CommentDelegateTests {

	/**
	 * Test what happens when the closure is empty.  This is fine, and we should
	 * have no comments when we're done.
	 */
	@Test
	void emptyComment() {
		def comment = buildComments(null) {}

		assertNull comment
	}

	/**
	 * Test what happens when we have a comment and no SQL..
	 */
	@Test
	void commentsNoSql() {
		def comment = buildComments(null) {
			comment 'No comment'
		}

		assertEquals 'No comment', comment
	}

	/**
	 * Test what happens when we have a two comments, and some SQL.  In this case
	 * the comments should be appended.  We'll also add some Sql to the mix.
	 */
	@Test
	void twoCommentsWithSql() {
		def comment = buildComments("delete from monkey;") {
			comment 'first'
			comment 'second'
	    "delete from monkey;"
		}

		assertEquals 'first second', comment
	}

	/**
	 * Try calling an invalid method in the closure.  Make sure we get our
	 * ChangeLogParseException and not Groovy's standard MethodMissingException.
	 */
	@Test(expected = ChangeLogParseException)
	void invalidClosure() {
		buildComments(null) {
			invalid "this is an invalid method"
		}
	}

	/**
	 * Helper method to execute an {@link ArgumentDelegate} and return any
	 * arguments it created.
	 * @param closure
	 * @return
	 */
	def buildComments(String expectedResult, Closure closure) {
		def delegate = new CommentDelegate(changeSetId: 'test-change-set',
						changeName: 'executeCommand')
		closure.delegate = delegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		def sql = closure.call()
		assertEquals expectedResult, sql

		return delegate.comment
	}
}
