package org.openhab.binding.entsoe.internal.monetary;

import javax.measure.UnitConverter;
import java.util.Collections;
import java.util.List;

public class IdentityConverter implements UnitConverter {
    public static final IdentityConverter INSTANCE = new IdentityConverter();

    private IdentityConverter() {
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
        return true;
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

    /**
     * Returns the inverse of this converter. If {@code x} is a valid value, then
     * {@code x == inverse().convert(convert(x))} to within the accuracy of computer arithmetic.
     *
     * @return the inverse of this converter.
     */
    @Override
    public UnitConverter inverse() {
        return this;
    }

    /**
     * Converts a {@code Number} value.
     *
     * @param value the {@code Number} value to convert.
     * @return the {@code Number} value after conversion.
     */
    @Override
    public Number convert(Number value) {
        return value;
    }

    /**
     * Converts a {@code double} value.
     *
     * @param value the numeric value to convert.
     * @return the {@code double} value after conversion.
     */
    @Override
    public double convert(double value) {
        return value;
    }

    /**
     * Concatenates this converter with another converter. The resulting converter is equivalent to first converting by
     * the specified converter (right converter), and then converting by this converter (left converter).
     *
     * @param converter the other converter to concatenate with this converter.
     * @return the concatenation of this converter with the other converter.
     */
    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        return converter;
    }

    /**
     * <p>
     * Returns the steps of fundamental converters making up this converter or {@code this} if the converter is a
     * fundamental converter.
     * </p>
     * <p>
     * For example, {@code converter1.getConversionSteps()} returns {@code converter1} while
     * {@code converter1.concatenate(converter2).getConversionSteps()} returns {@code converter1, converter2}.
     * </p>
     *
     * @return the list of fundamental converters which concatenated make up this converter.
     */
    @Override
    public List<? extends UnitConverter> getConversionSteps() {
        return Collections.singletonList(this);
    }

}
