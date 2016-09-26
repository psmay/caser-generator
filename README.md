CaserGenerator
==============

It's a program that accepts a specification in YAML format, meshes it
with a built-in Handlebars.java template, and produces code that is
intended to behave mostly like a suite of case classes (i.e. the Scala
concept) with a common final abstract class. The resulting code works in
JDK 7 and requires no additional dependencies, the generated
documentation is better than nothing if doc information is provided in
the spec, and the `toString()` implementations are useful, if minimal.
Extra classes/interfaces are included to accept values of the top type,
issue the cases as commands to such an acceptor, and implement a
dispatch interface to handle cases of individual types.

The included example spec `Term.yml` is intentionally modeled after an
example from [the case classes tutorial from
tour-of-scala](http://docs.scala-lang.org/tutorials/tour/case-classes.html)
in order to determine how successful the result turns out to be. In this
particular case, the caser union isn't as nice as the Scala idiom, but I
think it stands up in its own weird way. (Naturally, if Scala is what
you really want, you should be using Scala).

The Term example
----------------

Excerpt from 'Term.yaml':

    packageName: org.example.packagex
    name: Term
    doc: A term in an LCE
    modifiers: public
    cases:
      - name: Var
        doc: A variable in an LCE
        parameters:
          - name: name
            type: String
            doc: The name of the variable
      - name: Fun
    .
    .
    .

Excerpt from the generated code:

    package org.example.packagex;

    /**
     * A term in an LCE.
     */
    public abstract class Term {
        private Term() {}

    .
    .
    .

        /**
         * Classes representing the possible variants of {@link Term}.
         */
        public static final class Cases {
            ...
            /**
             * A variable in an LCE.
             */
            public static final class Var extends Term {
                ...
                /**
                 * Returns {@code name}: The name of the variable. ...
                 */
                public String name() {
                    return p_name;
                }
                ...
                /**
                 * Returns a new {@link Var} with the given parameters. ...
                 */
                public static final Var of(String name) {
                    return new Var(name);
                }
            }
    .
    .
    .

A port of the code from the Scala tutorial, with three different idioms
for the `isIdentityFun()` method based on the needs of the application:

    package org.example.packagex;

    // Brings in only Var, Fun, App
    import org.example.packagex.Term.Cases.*;

    public class TermTest {
        // Boilerplate implementations to make this look more like the original

        private static void print(Object o) { System.out.print(o); }
        private static void println() { System.out.println(); }
        private static void println(Object o) { System.out.println(o); }

        public static void main(String[] args) {
            TermTest z = new TermTest();
            z.run();
        }

        // end boilerplate

        public void printTerm(Term term) {
            // Term.Actions() is a superclass for dispatching a Term to a void function.
            // Term.Functions<T>(), seen later, is equivalent except that its methods return a T instance.
            // Either is used in place of the match() method from Scala, as demonstrated here:
            term.handle(new Term.Actions() {
                @Override public void ifVar(Var valueOfTerm, String n) {
                    print(n);
                }

                @Override public void ifFun(Fun valueOfTerm, String x, Term b) {
                    print("^" + x + ".");
                    printTerm(b);
                }

                @Override public void ifApp(App valueOfTerm, Term f, Term v) {
                    print("(");
                    printTerm(f);
                    print(" ");
                    printTerm(v);
                    print(")");
                }
            });
        }

        // Calls one of the three implementations given here.
        private boolean isIdentityFun(Term term) {
            //return isIdentityFunDirect(term);
            //return isIdentityFunShort(term);
            return isIdentityFunMedium(term);
        }

        // Direct translation from Scala.
        // A bit long-winded for this example; makes more sense when testing for
        // more than one variant.
        private boolean isIdentityFunDirect(Term term) {
            return term.handle(new Term.Functions<Boolean>() {
                @Override public Boolean ifFun(Fun valueOfFun, final String x, final Term b) {
                    boolean matchesPreconditions = b.handle(new Term.Functions<Boolean>() {
                        // A submatch is used here to safely unpack b to a `Var(y)`.
                        @Override public Boolean ifVar(Var valueOfVar, String y) {
                            return x.equals(y);
                        }

                        @Override public Boolean unhandled(Term valueOfTerm) {
                            return false;
                        }
                    });

                    if (!matchesPreconditions) {
                        // When implementing Term.Functions<> or Term.Actions, the handler
                        // method should defer to unhandled(Term) whenever none of the
                        // possible preconditions are true.
                        return unhandled(valueOfFun);
                    }
                    return true;
                }

                // unhandled(Term) is called by any if*() methods in Term.Functions<>
                // (or Term.Actions) that have not been overridden.
                @Override public Boolean unhandled(Term valueOfTerm) {
                    return false;
                }
            });
        }

        // Terse Java form that uses Term#asFun()/Term#asVar() instead of
        // Term#handler(). Works nicely when testing for a small number of
        // variants, but gets unwieldy for more exhaustive handling.
        private boolean isIdentityFunShort(Term term) {
            // term.asFun() is a soft cast; it means
            // (term instanceof Fun) ? (Fun)term : null
            // but is less ugly and doesn't repeat itself.
            Fun termFun = term.asFun();
            if (termFun != null) {
                String x = termFun.arg();
                Term b = termFun.body();
                // b.asVar() is another soft cast
                Var bVar = b.asVar();
                if (bVar != null) {
                    String y = bVar.name();
                    if (x.equals(y)) {
                        return true;
                    }
                }
            }
            return false;
        }

        // Happy medium that uses Term#handler() for the top-level match, then
        // Term#asVar() to check the precondition. This is an approach well suited
        // for when the top level is being exhaustively matched but sub-matching for
        // the preconditions can be simpler.
        private boolean isIdentityFunMedium(Term term) {
            return term.handle(new Term.Functions<Boolean>() {
                @Override public Boolean ifFun(Fun valueOfFun, String x, Term b) {
                    // The precondition evaluated in far fewer lines than before
                    Var bVar = b.asVar();
                    boolean matchesPreconditions = (bVar != null) && x.equals(bVar.name());

                    // Defer to unhandled() if necessary
                    if (!matchesPreconditions) {
                        return unhandled(valueOfFun);
                    }
                    return true;
                }

                @Override public Boolean unhandled(Term valueOfTerm) {
                    return false;
                }
            });
        }

        public void run() {
            final Term id = Fun.of("x", Var.of("x"));
            final Term t = Fun.of("x", Fun.of("y", App.of(Var.of("x"), Var.of("y"))));
            printTerm(id);
            println();
            printTerm(t);
            println();
            println(isIdentityFun(id));
            println(isIdentityFun(t));
        }
    }

TODO
----

More doc might be useful.

LICENSE
-------

Copyright 2016 Peter S. May, made available for use under the terms of
the Expat license.
