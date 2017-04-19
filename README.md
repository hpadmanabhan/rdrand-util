# Overview

Provides `java.util.Random` extension for Linux and Mac OSX wrapping Intel's Digital Random Number Generator (DRNG) known as Intel Secure Key, a name for its RDRAND instruction accessible via its free library [librdrand](http://software.intel.com/en-us/articles/intel-digital-random-number-generator-drng-software-implementation-guide/), using JNI. 

# About

First things first, a disclaimer: Neither I nor this utility are associated with or endorsed by Intel in any shape or form. Glad we got that out of the way, now to business.

You can get details on Intel's RNG implementation [here](http://software.intel.com/en-us/articles/intel-digital-random-number-generator-drng-software-implementation-guide/). This utility wraps functionality provided by RDRAND useful in the Java context via an extension of the standard interface `java.util.Random`. __Simple and clean, runs in user land, no root access is required, nor is any sys admin support required for shared library deployments, that is handled internally dynamically.__

Intel claims RDRAND is cryptographically strong and its hardware based implementation with a sealed seed/re-seed implementation eliminates many attack vectors possible for other RNGs, it also successfully passed the Dieharder tests as detailed below, however, it is a closed source implementation on chip so the true efficacy of the implementation cannot be verified independently. Nor is it possible to rule out any potential back doors implemented for the NSA as has been speculated by some around the web (although access to any such back door either physically or remotely would imply a serious breach of security that means you are already owned and compromise of your RNG is the least of your worries).

It is however being integrated in the Linux Kernel (3.12) as one of the sources for the entropy pool and like any black box implementation being used in a cryptographically sensitive environment, it should be tested regularly for randomness.

For those who would prefer a full open source RNG and for existing users of [Uncommons Maths](http://maths.uncommons.org/), version 1.1.0 of the utility provides an implementation of `org.uncommons.maths.random.SeedGenerator` using RDRAND which can be used as the seed source for any of the Uncommons Maths RNGs.

Since RDRAND cannot be seeded externally, no support is provided to specify a seed. Refer to the documentation linked above on seeding details. 

# Usage

RDRAND was introduced by Intel starting with the Ivy Bridge chipset so this will only work with these chips or newer. If you aren't sure about your hardware, the library build will tell whether it is supported or not.

## Distribution

Since native libraries are involved and the builds are not cross-platform, the utility is provided as source only for a Maven project including the librdrand source to be built locally.

## Dependencies

Dependencies are minimal, sl4j and log4j for logging and junit and commons-math3 for testing. 

## Build

Build requirements from a Java perspective are a JDK (1.7 if building unmodified) and Maven (developed and tested using Maven 3.0.4).

Makefiles and scripts for building librdrand and the JNI native code is included and the Maven build is configured to invoke them. The host on which the build is run must have gcc (min version 4.3.3), make and grep available. librdrand is built and linked into the native shared library generated using JNI, and referenced by the Java code.

__NOTE: The source and target for the compiler plugin in the POM are set to 1.7, update this if necessary to the version of Java you are running (Java 5 or 6).__

## Deployment

The project generates a single jar (excluding external dependencies) file which can be deployed and used by applications. The native shared library is included in the jar file from where it is extracted to the directory noted by the System property `java.io.tmpdir` and loaded dynamically at runtime.

__No need for the System Administrator to deploy the native shared library to system library paths or register with *ld*.__

## Code Usage

Since the implementation extends java.util.Random it can be used transparently anywhere you would use it.

```java
import java.util.Random;
import net.lizalab.util.RdRandRandom;
// ...
	Random random = new RdRandRandom();
```

Usage for the [Uncommons Maths](http://maths.uncommons.org/) SeedGenerator is similarly straightforward:

```java
import java.util.Random;
import net.lizalab.util.RdRandSeedGenerator
import org.uncommons.maths.random.AESCounterRNG;
// ...
	Random random = new AESCounterRNG(new RdRandSeedGenerator());
```

# Performance

For the built-in randomness tests running on a headless server with:

* Intel Xeon E3-1275 V2 Ivy Bridge processor (3.5GHz)
* CentOS 6.3
* Oracle Server VM 1.7 (Default Settings)

`RdRandRandom` was consistently about 10 times faster than SecureRandom (running the default SHA algorithm and using `/dev/random` as the entropy source). Running the tests should allow you to verify this easily for your hardware.

The benefits in a high usage environment over default *SecureRandom*, given the supported bandwidth outlined by Intel and shown by the tests, should be obvious especially on headless servers. 

# Built-In Tests

A couple of basic tests of randomness are built-in to the project, Mean Test and [Monte Carlo Pi Approximation](http://www.billthelizard.com/2009/05/how-do-you-test-random-number-generator.html) test. Verify the successful completion of the tests before proceeding to use the utility.

Successful run of the Mean Test should display results like this: 

```bash
-------------------------------------------------------
T E S T S
-------------------------------------------------------
Running net.lizalab.util.RdRandRandomTest
2013-09-13 19:38:54,743 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandom :  Normal mean: 4.5
2013-09-13 19:38:54,743 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandom :  Normal std: 3.0276503540974917
2013-09-13 19:38:54,743 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandom :  Normal var: 9.166666666666666
2013-09-13 19:38:54,744 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandom :  Generating 10000000 values.
2013-09-13 19:38:54,744 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandom :  Sample mean expected in range 4.497127718676731 - 4.502872281323269 99.7% of the times.
2013-09-13 19:38:54,744 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandom :  Sample mean expected in range 4.498085145784487 - 4.501914854215513 95% of the times.
.
.
2013-09-13 19:57:43,526 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  Time: 1971ms
2013-09-13 19:57:43,526 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  Distribution:
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  0: 999182
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  1: 999741
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  2: 1000009
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  3: 1000791
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  4: 999356
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  5: 1002547
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  6: 998484
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  7: 1000606
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  8: 1000099
2013-09-13 19:57:43,527 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  9: 999185
2013-09-13 19:57:43,528 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  mean: 4.500089399999296, diff: 1.9866666510203067E-5
2013-09-13 19:57:43,528 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  sd: 2.8717184431423703
2013-09-13 19:57:43,528 [INFO ] net.lizalab.util.RdRandRandomTest  - meanTest :  var: 8.24676681668404
```

Successful run of the Monte Carlo Pi Approximation Test should display results like this: 

```bash
2013-09-13 19:39:11,703 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandomMonteCarloPiTest :  Generating 1000000 points for Monte Carlo Pi Test. Expected precision: 3 digits, 3.14
.
.
2013-09-13 19:39:11,748 [INFO ] net.lizalab.util.RdRandRandomTest  - testRdRandRandomMonteCarloPiTest :  Running for RdRand..
2013-09-13 19:39:12,051 [INFO ] net.lizalab.util.RdRandRandomTest  - monteCarloPiTest :  Time: 303ms
2013-09-13 19:39:12,051 [INFO ] net.lizalab.util.RdRandRandomTest  - monteCarloPiTest :  Pi Approximation: 3.142456, Diff: 8.633464102070221E-4, Error %: 0.027481169757018142
```

The tests are also run for `java.util.Random` and `java.security.SecureRandom`, however, unlike for `net.lizalab.util.RdRandRandom`, their tests results are not asserted, they are simply for reference.

# Standard Tests

`RdRandRandom`, the `java.util.Random` extension provided by this utility has been put through the [Dieharder](http://www.phy.duke.edu/~rgb/General/dieharder.php) suite of tests, considered the best test suite out there currently for RNGs for research and cryptography, and has successfully passed it.

```bash
$ dieharder -l
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
Installed dieharder tests:
 Test Number                         Test Name                Test Reliability
===============================================================================
  -d 0                            Diehard Birthdays Test              Good
  -d 1                               Diehard OPERM5 Test              Good
  -d 2                    Diehard 32x32 Binary Rank Test              Good
  -d 3                      Diehard 6x8 Binary Rank Test              Good
  -d 4                            Diehard Bitstream Test              Good
  -d 5                                      Diehard OPSO           Suspect
  -d 6                                 Diehard OQSO Test           Suspect
  -d 7                                  Diehard DNA Test           Suspect
  -d 8                Diehard Count the 1s (stream) Test              Good
  -d 9                  Diehard Count the 1s Test (byte)              Good
  -d 10                         Diehard Parking Lot Test              Good
  -d 11         Diehard Minimum Distance (2d Circle) Test             Good
  -d 12         Diehard 3d Sphere (Minimum Distance) Test             Good
  -d 13                             Diehard Squeeze Test              Good
  -d 14                                Diehard Sums Test        Do Not Use
  -d 15                                Diehard Runs Test              Good
  -d 16                               Diehard Craps Test              Good
  -d 17                     Marsaglia and Tsang GCD Test              Good
  -d 100                                STS Monobit Test              Good
  -d 101                                   STS Runs Test              Good
  -d 102                   STS Serial Test (Generalized)              Good
  -d 200                       RGB Bit Distribution Test              Good
  -d 201           RGB Generalized Minimum Distance Test              Good
  -d 202                           RGB Permutations Test              Good
  -d 203                             RGB Lagged Sum Test              Good
  -d 204                RGB Kolmogorov-Smirnov Test Test              Good
  -d 205                               Byte Distribution              Good
  -d 206                                         DAB DCT              Good
  -d 207                              DAB Fill Tree Test              Good
  -d 208                            DAB Fill Tree 2 Test              Good
  -d 209                              DAB Monobit 2 Test              Good
```

The test results and the code snippet used to facilitate the test are provided below. The latter can be used to run the tests independently.

## Dieharder Test results (Default 100 p samples)

```bash
$ java -cp ".:lizalab-rdrand-util-1.0.1.0.jar:slf4j-api-1.7.2.jar:slf4j-log4j12-1.7.2.jar:log4j-1.2.17.jar" RdRandRandomByteStream | dieharder -a -g 200
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  2.44e+07  | 977030350|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.36411172|  PASSED  
      diehard_operm5|   0|   1000000|     100|0.88369087|  PASSED  
  diehard_rank_32x32|   0|     40000|     100|0.71750775|  PASSED  
    diehard_rank_6x8|   0|    100000|     100|0.84716239|  PASSED  
   diehard_bitstream|   0|   2097152|     100|0.78129829|  PASSED  
        diehard_opso|   0|   2097152|     100|0.43713031|  PASSED  
        diehard_oqso|   0|   2097152|     100|0.82525161|  PASSED  
         diehard_dna|   0|   2097152|     100|0.07980804|  PASSED  
diehard_count_1s_str|   0|    256000|     100|0.14105319|  PASSED  
diehard_count_1s_byt|   0|    256000|     100|0.19181662|  PASSED  
 diehard_parking_lot|   0|     12000|     100|0.51759383|  PASSED  
    diehard_2dsphere|   2|      8000|     100|0.43073986|  PASSED  
    diehard_3dsphere|   3|      4000|     100|0.71441897|  PASSED  
     diehard_squeeze|   0|    100000|     100|0.07423913|  PASSED  
        diehard_sums|   0|       100|     100|0.47308063|  PASSED  
        diehard_runs|   0|    100000|     100|0.64974722|  PASSED  
        diehard_runs|   0|    100000|     100|0.73524395|  PASSED  
       diehard_craps|   0|    200000|     100|0.13334508|  PASSED  
       diehard_craps|   0|    200000|     100|0.92114183|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|     100|0.07463720|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|     100|0.48134560|  PASSED  
         sts_monobit|   1|    100000|     100|0.58809382|  PASSED  
            sts_runs|   2|    100000|     100|0.34510658|  PASSED  
          sts_serial|   1|    100000|     100|0.30344924|  PASSED  
          sts_serial|   2|    100000|     100|0.20541369|  PASSED  
          sts_serial|   3|    100000|     100|0.40854809|  PASSED  
          sts_serial|   3|    100000|     100|0.47236232|  PASSED  
          sts_serial|   4|    100000|     100|0.97359140|  PASSED  
          sts_serial|   4|    100000|     100|0.44514849|  PASSED  
          sts_serial|   5|    100000|     100|0.79162722|  PASSED  
          sts_serial|   5|    100000|     100|0.42896763|  PASSED  
          sts_serial|   6|    100000|     100|0.92300349|  PASSED  
          sts_serial|   6|    100000|     100|0.86970826|  PASSED  
          sts_serial|   7|    100000|     100|0.84263155|  PASSED  
          sts_serial|   7|    100000|     100|0.68520161|  PASSED  
          sts_serial|   8|    100000|     100|0.18243093|  PASSED  
          sts_serial|   8|    100000|     100|0.03072194|  PASSED  
          sts_serial|   9|    100000|     100|0.44281143|  PASSED  
          sts_serial|   9|    100000|     100|0.83742799|  PASSED  
          sts_serial|  10|    100000|     100|0.60364027|  PASSED  
          sts_serial|  10|    100000|     100|0.36440095|  PASSED  
          sts_serial|  11|    100000|     100|0.82346253|  PASSED  
          sts_serial|  11|    100000|     100|0.88452367|  PASSED  
          sts_serial|  12|    100000|     100|0.98997535|  PASSED  
          sts_serial|  12|    100000|     100|0.88536579|  PASSED  
          sts_serial|  13|    100000|     100|0.15217386|  PASSED  
          sts_serial|  13|    100000|     100|0.72443691|  PASSED  
          sts_serial|  14|    100000|     100|0.84832373|  PASSED  
          sts_serial|  14|    100000|     100|0.88975306|  PASSED  
          sts_serial|  15|    100000|     100|0.28587103|  PASSED  
          sts_serial|  15|    100000|     100|0.13859319|  PASSED  
          sts_serial|  16|    100000|     100|0.96573888|  PASSED  
          sts_serial|  16|    100000|     100|0.51355422|  PASSED  
         rgb_bitdist|   1|    100000|     100|0.26666100|  PASSED  
         rgb_bitdist|   2|    100000|     100|0.99247247|  PASSED  
         rgb_bitdist|   3|    100000|     100|0.99205147|  PASSED  
         rgb_bitdist|   4|    100000|     100|0.94427180|  PASSED  
         rgb_bitdist|   5|    100000|     100|0.41728892|  PASSED  
         rgb_bitdist|   6|    100000|     100|0.00116208|   WEAK   
         rgb_bitdist|   7|    100000|     100|0.25862202|  PASSED  
         rgb_bitdist|   8|    100000|     100|0.55875428|  PASSED  
         rgb_bitdist|   9|    100000|     100|0.25095178|  PASSED  
         rgb_bitdist|  10|    100000|     100|0.94487328|  PASSED  
         rgb_bitdist|  11|    100000|     100|0.78973737|  PASSED  
         rgb_bitdist|  12|    100000|     100|0.69845271|  PASSED  
rgb_minimum_distance|   2|     10000|    1000|0.33817481|  PASSED  
rgb_minimum_distance|   3|     10000|    1000|0.06475846|  PASSED  
rgb_minimum_distance|   4|     10000|    1000|0.90024136|  PASSED  
rgb_minimum_distance|   5|     10000|    1000|0.01206348|  PASSED  
    rgb_permutations|   2|    100000|     100|0.84085898|  PASSED  
    rgb_permutations|   3|    100000|     100|0.81397567|  PASSED  
    rgb_permutations|   4|    100000|     100|0.08274235|  PASSED  
    rgb_permutations|   5|    100000|     100|0.25045381|  PASSED  
      rgb_lagged_sum|   0|   1000000|     100|0.14567587|  PASSED  
      rgb_lagged_sum|   1|   1000000|     100|0.91139281|  PASSED  
      rgb_lagged_sum|   2|   1000000|     100|0.72974431|  PASSED  
      rgb_lagged_sum|   3|   1000000|     100|0.38736451|  PASSED  
      rgb_lagged_sum|   4|   1000000|     100|0.98807549|  PASSED  
      rgb_lagged_sum|   5|   1000000|     100|0.79815846|  PASSED  
      rgb_lagged_sum|   6|   1000000|     100|0.73861247|  PASSED  
      rgb_lagged_sum|   7|   1000000|     100|0.36577571|  PASSED  
      rgb_lagged_sum|   8|   1000000|     100|0.38560822|  PASSED  
      rgb_lagged_sum|   9|   1000000|     100|0.06685343|  PASSED  
      rgb_lagged_sum|  10|   1000000|     100|0.11253476|  PASSED  
      rgb_lagged_sum|  11|   1000000|     100|0.05943949|  PASSED  
      rgb_lagged_sum|  12|   1000000|     100|0.05578541|  PASSED  
      rgb_lagged_sum|  13|   1000000|     100|0.76076261|  PASSED  
      rgb_lagged_sum|  14|   1000000|     100|0.99452473|  PASSED  
      rgb_lagged_sum|  15|   1000000|     100|0.13197216|  PASSED  
      rgb_lagged_sum|  16|   1000000|     100|0.24799510|  PASSED  
      rgb_lagged_sum|  17|   1000000|     100|0.99890408|   WEAK   
      rgb_lagged_sum|  18|   1000000|     100|0.97884236|  PASSED  
      rgb_lagged_sum|  19|   1000000|     100|0.08685069|  PASSED  
      rgb_lagged_sum|  20|   1000000|     100|0.86653358|  PASSED  
      rgb_lagged_sum|  21|   1000000|     100|0.44697224|  PASSED  
      rgb_lagged_sum|  22|   1000000|     100|0.90474984|  PASSED  
      rgb_lagged_sum|  23|   1000000|     100|0.79097716|  PASSED  
      rgb_lagged_sum|  24|   1000000|     100|0.85425267|  PASSED  
      rgb_lagged_sum|  25|   1000000|     100|0.79069951|  PASSED  
      rgb_lagged_sum|  26|   1000000|     100|0.94148304|  PASSED  
      rgb_lagged_sum|  27|   1000000|     100|0.82933818|  PASSED  
      rgb_lagged_sum|  28|   1000000|     100|0.43473739|  PASSED  
      rgb_lagged_sum|  29|   1000000|     100|0.74173963|  PASSED  
      rgb_lagged_sum|  30|   1000000|     100|0.91688071|  PASSED  
      rgb_lagged_sum|  31|   1000000|     100|0.21801685|  PASSED  
      rgb_lagged_sum|  32|   1000000|     100|0.91977712|  PASSED  
     rgb_kstest_test|   0|     10000|    1000|0.34091360|  PASSED  
     dab_bytedistrib|   0|  51200000|       1|0.82690978|  PASSED  
             dab_dct| 256|     50000|       1|0.12824228|  PASSED  
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.91449223|  PASSED  
        dab_filltree|  32|  15000000|       1|0.70050593|  PASSED  
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.99962726|   WEAK   
       dab_filltree2|   1|   5000000|       1|0.37492397|  PASSED  
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.85902180|  PASSED 
```

## Dieharder Test results (m 10, 1000 p samples)

```bash
$ java -cp ".:lizalab-rdrand-util-1.0.1.0.jar:slf4j-api-1.7.2.jar:slf4j-log4j12-1.7.2.jar:log4j-1.2.17.jar" RdRandRandomByteStream | dieharder -a -g 200 -m 10
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  4.65e+07  | 888358514|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|    1000|0.87386750|  PASSED  
      diehard_operm5|   0|   1000000|    1000|0.62660880|  PASSED  
  diehard_rank_32x32|   0|     40000|    1000|0.04168779|  PASSED  
    diehard_rank_6x8|   0|    100000|    1000|0.97638394|  PASSED  
   diehard_bitstream|   0|   2097152|    1000|0.05625039|  PASSED  
        diehard_opso|   0|   2097152|    1000|0.56995244|  PASSED  
        diehard_oqso|   0|   2097152|    1000|0.23630450|  PASSED  
         diehard_dna|   0|   2097152|    1000|0.03606780|  PASSED  
diehard_count_1s_str|   0|    256000|    1000|0.05799853|  PASSED  
diehard_count_1s_byt|   0|    256000|    1000|0.43067541|  PASSED  
 diehard_parking_lot|   0|     12000|    1000|0.10472999|  PASSED  
    diehard_2dsphere|   2|      8000|    1000|0.88550088|  PASSED  
    diehard_3dsphere|   3|      4000|    1000|0.99338887|  PASSED  
     diehard_squeeze|   0|    100000|    1000|0.58098232|  PASSED  
        diehard_sums|   0|       100|    1000|0.00000002|  FAILED  
        diehard_runs|   0|    100000|    1000|0.58549842|  PASSED  
        diehard_runs|   0|    100000|    1000|0.05140719|  PASSED  
       diehard_craps|   0|    200000|    1000|0.89574614|  PASSED  
       diehard_craps|   0|    200000|    1000|0.61781962|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|    1000|0.07942143|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|    1000|0.01525728|  PASSED  
         sts_monobit|   1|    100000|    1000|0.71593252|  PASSED  
            sts_runs|   2|    100000|    1000|0.12646554|  PASSED  
          sts_serial|   1|    100000|    1000|0.79719169|  PASSED  
          sts_serial|   2|    100000|    1000|0.77251784|  PASSED  
          sts_serial|   3|    100000|    1000|0.66561832|  PASSED  
          sts_serial|   3|    100000|    1000|0.11767838|  PASSED  
          sts_serial|   4|    100000|    1000|0.94057906|  PASSED  
          sts_serial|   4|    100000|    1000|0.64115787|  PASSED  
          sts_serial|   5|    100000|    1000|0.56034921|  PASSED  
          sts_serial|   5|    100000|    1000|0.44109159|  PASSED  
          sts_serial|   6|    100000|    1000|0.38191552|  PASSED  
          sts_serial|   6|    100000|    1000|0.58529672|  PASSED  
          sts_serial|   7|    100000|    1000|0.49362195|  PASSED  
          sts_serial|   7|    100000|    1000|0.36082916|  PASSED  
          sts_serial|   8|    100000|    1000|0.81854736|  PASSED  
          sts_serial|   8|    100000|    1000|0.96961365|  PASSED  
          sts_serial|   9|    100000|    1000|0.98111802|  PASSED  
          sts_serial|   9|    100000|    1000|0.36683143|  PASSED  
          sts_serial|  10|    100000|    1000|0.44715344|  PASSED  
          sts_serial|  10|    100000|    1000|0.42659613|  PASSED  
          sts_serial|  11|    100000|    1000|0.12310022|  PASSED  
          sts_serial|  11|    100000|    1000|0.45300647|  PASSED  
          sts_serial|  12|    100000|    1000|0.03895779|  PASSED  
          sts_serial|  12|    100000|    1000|0.24317614|  PASSED  
          sts_serial|  13|    100000|    1000|0.21054381|  PASSED  
          sts_serial|  13|    100000|    1000|0.87058684|  PASSED  
          sts_serial|  14|    100000|    1000|0.06381627|  PASSED  
          sts_serial|  14|    100000|    1000|0.41595994|  PASSED  
          sts_serial|  15|    100000|    1000|0.91194072|  PASSED  
          sts_serial|  15|    100000|    1000|0.71655000|  PASSED  
          sts_serial|  16|    100000|    1000|0.45437075|  PASSED  
          sts_serial|  16|    100000|    1000|0.44811188|  PASSED  
         rgb_bitdist|   1|    100000|    1000|0.09351524|  PASSED  
         rgb_bitdist|   2|    100000|    1000|0.42487109|  PASSED  
         rgb_bitdist|   3|    100000|    1000|0.11919539|  PASSED  
         rgb_bitdist|   4|    100000|    1000|0.94317403|  PASSED  
         rgb_bitdist|   5|    100000|    1000|0.81957337|  PASSED  
         rgb_bitdist|   6|    100000|    1000|0.73808098|  PASSED  
         rgb_bitdist|   7|    100000|    1000|0.44632480|  PASSED  
         rgb_bitdist|   8|    100000|    1000|0.99771137|   WEAK   
         rgb_bitdist|   9|    100000|    1000|0.80671475|  PASSED  
         rgb_bitdist|  10|    100000|    1000|0.69340937|  PASSED  
         rgb_bitdist|  11|    100000|    1000|0.64178846|  PASSED  
         rgb_bitdist|  12|    100000|    1000|0.48783457|  PASSED  
rgb_minimum_distance|   2|     10000|   10000|0.40662829|  PASSED  
rgb_minimum_distance|   3|     10000|   10000|0.56538655|  PASSED  
rgb_minimum_distance|   4|     10000|   10000|0.06432970|  PASSED  
rgb_minimum_distance|   5|     10000|   10000|0.15402124|  PASSED  
    rgb_permutations|   2|    100000|    1000|0.74005516|  PASSED  
    rgb_permutations|   3|    100000|    1000|0.95772253|  PASSED  
    rgb_permutations|   4|    100000|    1000|0.26515691|  PASSED  
    rgb_permutations|   5|    100000|    1000|0.06644306|  PASSED  
      rgb_lagged_sum|   0|   1000000|    1000|0.38110382|  PASSED  
      rgb_lagged_sum|   1|   1000000|    1000|0.96945747|  PASSED  
      rgb_lagged_sum|   2|   1000000|    1000|0.05459364|  PASSED  
      rgb_lagged_sum|   3|   1000000|    1000|0.09741920|  PASSED  
      rgb_lagged_sum|   4|   1000000|    1000|0.97143688|  PASSED  
      rgb_lagged_sum|   5|   1000000|    1000|0.31951837|  PASSED  
      rgb_lagged_sum|   6|   1000000|    1000|0.99301824|  PASSED  
      rgb_lagged_sum|   7|   1000000|    1000|0.44500945|  PASSED  
      rgb_lagged_sum|   8|   1000000|    1000|0.39156958|  PASSED  
      rgb_lagged_sum|   9|   1000000|    1000|0.51161200|  PASSED  
      rgb_lagged_sum|  10|   1000000|    1000|0.95501539|  PASSED  
      rgb_lagged_sum|  11|   1000000|    1000|0.30667257|  PASSED  
      rgb_lagged_sum|  12|   1000000|    1000|0.78346664|  PASSED  
      rgb_lagged_sum|  13|   1000000|    1000|0.90368944|  PASSED  
      rgb_lagged_sum|  14|   1000000|    1000|0.90493840|  PASSED  
      rgb_lagged_sum|  15|   1000000|    1000|0.23429415|  PASSED  
      rgb_lagged_sum|  16|   1000000|    1000|0.36976045|  PASSED  
      rgb_lagged_sum|  17|   1000000|    1000|0.01728072|  PASSED  
      rgb_lagged_sum|  18|   1000000|    1000|0.91546611|  PASSED  
      rgb_lagged_sum|  19|   1000000|    1000|0.79138582|  PASSED  
      rgb_lagged_sum|  20|   1000000|    1000|0.87478973|  PASSED  
      rgb_lagged_sum|  21|   1000000|    1000|0.92592114|  PASSED  
      rgb_lagged_sum|  22|   1000000|    1000|0.91216185|  PASSED  
      rgb_lagged_sum|  23|   1000000|    1000|0.94053990|  PASSED  
      rgb_lagged_sum|  24|   1000000|    1000|0.64759065|  PASSED  
      rgb_lagged_sum|  25|   1000000|    1000|0.90840442|  PASSED  
      rgb_lagged_sum|  26|   1000000|    1000|0.31445527|  PASSED  
      rgb_lagged_sum|  27|   1000000|    1000|0.91439647|  PASSED  
      rgb_lagged_sum|  28|   1000000|    1000|0.54431309|  PASSED  
      rgb_lagged_sum|  29|   1000000|    1000|0.95389466|  PASSED  
      rgb_lagged_sum|  30|   1000000|    1000|0.71193616|  PASSED  
      rgb_lagged_sum|  31|   1000000|    1000|0.72716614|  PASSED  
      rgb_lagged_sum|  32|   1000000|    1000|0.22662076|  PASSED  
     rgb_kstest_test|   0|     10000|   10000|0.00026752|   WEAK   
     dab_bytedistrib|   0|  51200000|      10|0.74151942|  PASSED  
             dab_dct| 256|     50000|      10|0.99535132|   WEAK   
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|      10|0.13934018|  PASSED  
        dab_filltree|  32|  15000000|      10|0.71655616|  PASSED  
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|      10|0.77329317|  PASSED  
       dab_filltree2|   1|   5000000|      10|0.81542316|  PASSED  
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|      10|0.86973855|  PASSED
```

The one failed test is a defective test that fails at higher p sample levels of 1000 and above and is marked as do not use.

## Dieharder Test Byte Stream Wrapper Code

```java
import java.io.IOException;
import java.util.Random;

import net.lizalab.util.RdRandRandom;

public class RdRandRandomByteStream {
        public static void main(String[] args) throws IOException {
                Random random = new RdRandRandom();
                for (;;) {
                        byte[] num = new byte[4096];
                        random.nextBytes(num);
                        System.out.write(num);
                }
        }
}
```

__NOTE: The Java random byte stream needs to be terminated manually once the final test has completed since in its current incarnation it runs in an infinite for loop.__

# Revision History

* Version 1.1 - Added Uncommons Maths SeedGenerator implementation to enable using RDRAND as seed source with UnCommons Maths RNGs.
* Version 1.0.1 - Fixed bug in mean test implementation in RdRandRandomTest where distributions array was not being reset between runs for different RNGs.
* Version 1.0 - Initial release of the utility providing an implementation of java.util.Random using RDRAND via JNI.
