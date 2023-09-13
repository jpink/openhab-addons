package org.openhab.binding.entsoe.internal.price.service.measure;

import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.MultiplyConverter;

import javax.measure.UnitConverter;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyCentConverter extends AbstractConverter {//implements MultiplyConverter {
    public static final CurrencyCentConverter INSTANCE = new CurrencyCentConverter();

    private static final BigDecimal FACTOR = new BigDecimal(100);

    private CurrencyCentConverter() {
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object cvtr) {
        return cvtr == INSTANCE;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Non-API
     * <p>
     * Returns a String describing the transformation that is represented by this converter. Contributes to converter's
     * {@code toString} method. If null or empty {@code toString} output becomes simplified.
     * </p>
     *
     * @return
     */
    @Override
    protected String transformationLiteral() {
        return "Centi-currency to base currency";
    }

    /**
     * Non-API
     * <p>
     * Returns an AbstractConverter that represents the inverse transformation of this converter, for cases where the
     * transformation is not the identity transformation.
     * </p>
     *
     * @return
     */
    @Override
    protected AbstractConverter inverseWhenNotIdentity() {
        return null;
    }

    /**
     * Non-API Guard for {@link #reduce(AbstractConverter)}
     *
     * @param that
     * @return whether or not a composition with given {@code that} is possible, such that no additional conversion
     *         steps are required, with respect to the steps already in place by this converter
     */
    @Override
    protected boolean canReduceWith(AbstractConverter that) {
        return false;
    }

    /**
     * Non-API
     *
     * @param value
     * @return transformed value
     */
    @Override
    protected BigDecimal convertWhenNotIdentity(Number value) {
        return ((BigDecimal) value).divide(FACTOR, RoundingMode.HALF_UP);
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for all {@code x} and {@code y}.  (This implies that
     * {@code x.compareTo(y)} must throw an exception if and only if {@code y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z)) == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it from being compared to this
     *         object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     *         {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any class that implements the
     *         {@code Comparable} interface and violates this condition should clearly indicate this fact.  The
     *         recommended language is "Note: this class has a natural ordering that is inconsistent with equals."
     */
    @Override
    public int compareTo(UnitConverter o) {
        return 0;
    }

    /**
     * Indicates if this converter is an identity converter. The identity converter returns its input argument
     * ({@code convert(x) == x}).
     * <p>
     * Note: Identity converters are also always 'linear', see {@link UnitConverter#isLinear()}.
     * </p>
     *
     * @return {@code true} if this converter is an identity converter.
     */
    @Override
    public boolean isIdentity() {
        return false;
    }

    /**
     * Indicates whether this converter represents a (one-dimensional) linear transformation, that is a <a
     * href="https://en.wikipedia.org/wiki/Linear_map">linear map (wikipedia)</a> from a one-dimensional vector space (a
     * scalar) to a one-dimensional vector space. Typically from 'R' to 'R', with 'R' the real numbers.
     *
     * <p>
     * Given such a 'linear' converter 'A', let 'u', 'v' and 'r' be arbitrary numbers, then the following must hold by
     * definition:
     *
     * <ul>
     * <li>{@code A(u + v) == A(u) + A(v)}</li>
     * <li>{@code A(r * u) == r * A(u)}</li>
     * </ul>
     *
     * <p>
     * Given a second 'linear' converter 'B', commutativity of composition follows by above definition:
     *
     * <ul>
     * <li>{@code (A o B) (u) == (B o A) (u)}</li>
     * </ul>
     *
     * In other words, two 'linear' converters do have the property that {@code A(B(u)) == B(A(u))}, meaning
     * for 'A' and 'B' the order of their composition does not matter. Expressed as Java code:
     *
     * <p>
     * {@code A.concatenate(B).convert(u) == B.concatenate(A).convert(u)}
     * </p>
     *
     * Note: For composing UnitConverters see also {@link UnitConverter#concatenate(UnitConverter)}.
     *
     * @return {@code true} if this converter represents a linear transformation; {@code false} otherwise.
     */
    @Override
    public boolean isLinear() {
        return true;
    }

}
