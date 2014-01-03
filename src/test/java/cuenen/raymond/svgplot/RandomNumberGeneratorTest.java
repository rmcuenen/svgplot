/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the Common Development and Distribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is SVG Plot Module Extension.
 *
 * The Initial Developer of the Original Code is R. M. Cuenen
 * Portions created by the Initial Developer are Copyright (C) 2013
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Raymond Cuenen <Raymond.Cuenen@gmail.com>
 *
 * If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 */
package cuenen.raymond.svgplot;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code RandomNumberGenerator.js}.
 *
 * @author R. M. Cuenen
 */
public class RandomNumberGeneratorTest extends AbstractTestClass {

    private static final String MODULE_NAME = "RandomNumberGenerator";
    private static final String FUNCTION_FORMAT = "function(RNG) { var d = %s; for (var i = 0; i < 1e6; i++) { var r = RNG.random(); %s } %s setResult(d); }";
    private static final Object[] RANGE_TEST = {"[Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY]",
        "d[0] = Math.min(d[0], r); d[1] = Math.max(d[1], r);", ""};
    private static final Object[] MEAN_TEST = {"0", "d += r;", "d /= 1e6;"};
    private static final Object[] VARIANCE_TEST = {"0; var m = 0; var s = []", "s.push(r); m += r;",
        "m /= 1e6; for (var i = 0; i < 1e6; i++) { d += (s[i] - m) * (s[i] - m); } d /= (1e6 - 1);"};
    private static final Object[] BUCKET_TEST = {"0; var b = Array.apply(null, new Array(1e4)).map(Number.prototype.valueOf, 0);",
        "b[Math.floor(r * 1e4)]++;",
        "d = b.reduce(function(t, o) { return t + ((o - 100) * (o - 100) / 100); }, 0);"};
    private static final Object[] KS_TEST = {"0; var b = Array.apply(null, new Array(1e4)).map(Number.prototype.valueOf, 0);",
        "b[Math.floor(r * 1e4)]++;",
        "b.reduce(function(t, o, i) { d = Math.max(d, Math.abs(t - i / 1e4)); return t += o / 1e6; }, 0); d *= 1000;"};

    /**
     * If a probability distribution has a limited range, the simplest thing to
     * test is whether the output values fall in that range. There is one aspect
     * of output ranges that cannot be tested effectively by black-box testing:
     * boundary values. It may be impractical to test whether the endpoints of
     * intervals are included.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void rangeTest(WebDriver driver) {
        String result = executeTest(driver, RANGE_TEST);
        String msg = getMessage(driver);
        String[] range = result.split(",");
        /* Verify the range [0, 1). */
        assertEquals(Double.parseDouble(range[0]), 0D, 1E-5, msg);
        assertEquals(Double.parseDouble(range[1]), 1D, 1E-5, msg);
    }

    /**
     * One of the most obvious things to do to test a random number generator is
     * to average a large number of values to see whether the average is close
     * to the theoretical value.
     * <p>
     * The central limit theorem says if we average enough values, any
     * distribution acts like a normal distribution. We can test whether the
     * average falls between two or three standard deviations of what we expect.
     * <p>
     * In summary, the way to test samples from a random number generator with
     * mean &mu; and standard deviation &sigma; is to average n values for some
     * large value of n. Then look for the average to be between &mu; −
     * 2&sigma;/&radic;n and &mu; + 2&sigma;/&radic;n around 95% of the time, or
     * between &mu; − 3&sigma;/&radic;n and &mu; + 3&sigma;/&radic;n around
     * 99.7% of the time.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void meanTest(WebDriver driver) {
        String result = executeTest(driver, MEAN_TEST);
        String msg = getMessage(driver);
        /* The standard uniform distribution has mean 1/2 and variance 1/12. */
        double expectedAVG = 0.5;
        double expectedSTD = Math.sqrt(1D / 12D) / 1000D;
        assertEquals(Double.parseDouble(result), expectedAVG, 2D * expectedSTD, msg);
    }

    /**
     * Just as the mean test compares the mean of the samples to the mean of the
     * distribution, the variance test compares the variance of the samples to
     * the variance of the distribution.
     * <p>
     * As with testing the mean, we can test whether the sample variance falls
     * between two or three standard deviations of what we expect.
     * <p>
     * Suppose we compute the sample variance of some large number of outputs
     * from an RNG. The outputs are random, so the sample variance is also
     * random. Let S&sup2; be the sample variance based on n values from the
     * RNG. If n is very large, then S&sup2; approximately has a normal
     * distribution with mean &sigma;&sup2; and variance 2&sigma;&#x2074;/(n−1).
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void varianceTest(WebDriver driver) {
        String result = executeTest(driver, VARIANCE_TEST);
        String msg = getMessage(driver);
        /* The standard uniform distribution has variance 1/12. */
        double expectedVAR = 1D / 12D;
        double expectedSTD = Math.sqrt(2D * expectedVAR * expectedVAR / (1E6 - 1D));
        assertEquals(Double.parseDouble(result), expectedVAR, 2D * expectedSTD, msg);
    }

    /**
     * Suppose the RNG passes both the mean and the variance test. That gives
     * you some confidence that the code generates samples from <i>some</i>
     * distribution with the right mean and variance, but it’s still possible
     * the samples are coming from an entirely wrong distribution.
     * <p>
     * You could count how many values fall into various "buckets", or ranges of
     * values. (What we call "the bucket test" here is commonly known as the
     * chi-square (&Chi;&sup2;) test.)
     * <p>
     * Here’s how to do a bucket test. Divide your output range into k buckets.
     * The buckets should cover the entire range of the output and not overlap.
     * Let E&#x2097; be the expected number of samples for the lth bucket, and
     * let O&#x2097; be the number of samples you actually observe. Then,
     * compute the chi-square statistic: &Chi;&sup2; = &sum; (O&#x2097; -
     * E&#x2097;)&sup2; / E&#x2097;.
     * <p>
     * If we have b buckets, the statistic &Chi;&sup2; has a chi-square
     * distribution with b−1 degrees of freedom. For large b, a chi-square
     * distribution with b−1 degrees of freedom has approximately the same
     * distribution as a normal distribution with mean b−1 and variance 2b−2.
     * Then we can use the same rules as before regarding how often a normal
     * random variable is within two or three standard deviations of its mean.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void bucketTest(WebDriver driver) {
        String result = executeTest(driver, BUCKET_TEST);
        String msg = getMessage(driver);
        /* We test with 10^4 buckets. */
        double expectedMEAN = 1E4 - 1D;
        double expectedSTD = Math.sqrt(2E4 - 2D);
        assertEquals(Double.parseDouble(result), expectedMEAN, 2D * expectedSTD, msg);
    }

    /**
     * One shortcoming of the bucket test is that it is "chunky". It is possible
     * that a random number generator puts approximately the expected number of
     * samples in each bucket and yet the samples are not properly distributed
     * within the buckets. We would like a more fine-grained test of how the
     * random samples are distributed.
     * <p>
     * We will compare the empirical distribution function with the theoretical
     * distribution function. The empirical distribution is defined as:
     * F&#x2099;(x) = (the number of x&#x2097; values &le; x) / n and the
     * theoretical distribution function F(x) is the theoretical probability of
     * the RNG returning a value no greater than x. We want to look at the
     * differences between F(x) and F&#x2099;(x). In short, we want to do a
     * direct comparison of the theoretical and empirical distribution of
     * values. This is the idea behind the Kolmogorov-Smirnov (K-S) test.
     * <p>
     * With the previous tests, we appealed to the central limit theorem to
     * reduce our problem to a normal distribution. That’s not going to work
     * here. We’re going to pull a rabbit out of the hat and give range values
     * without saying where they came from. For large n, we expect the maximum
     * absolute difference to be between 0.07089 and 1.5174 around 98% of the
     * time.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void ksTest(WebDriver driver) {
        String result = executeTest(driver, KS_TEST);
        String msg = getMessage(driver);
        /* Midpoint and delta of the range [0.07089, 1.5174]. */
        double expected = 0.794145;
        double delta = 0.723255;
        assertEquals(Double.parseDouble(result), expected, delta, msg);
    }

    /**
     * Convenience function for executing the tests.
     *
     * @param driver The WebDriver executing the test.
     * @param testDescription The array of objects to be used with the
     * {@link #FUNCTION_FORMAT}.
     * @return The result from the placeholder WebElement.
     */
    private String executeTest(WebDriver driver, Object... testDescription) {
        Wait wait = load(driver, MODULE_LOADER, 1);
        String callback = String.format(FUNCTION_FORMAT, testDescription);
        require(driver, callback, MODULE_NAME);
        wait.until(RESULT_SET);
        return getResult(driver);
    }
}
