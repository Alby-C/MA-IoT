<<<<<<< HEAD:BikeActivity/app/src/androidTest/java/com/multimediaapp/bikeactivity/ExampleInstrumentedTest.java
package com.multimediaapp.bikeactivity;
=======
package com.example.gyroscope;
>>>>>>> Alby:Gyroscope/app/src/androidTest/java/com/example/gyroscope/ExampleInstrumentedTest.java

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
<<<<<<< HEAD:BikeActivity/app/src/androidTest/java/com/multimediaapp/bikeactivity/ExampleInstrumentedTest.java
        assertEquals("com.multimediaapp.bikeactivity", appContext.getPackageName());
=======
        assertEquals("com.example.gyroscope", appContext.getPackageName());
>>>>>>> Alby:Gyroscope/app/src/androidTest/java/com/example/gyroscope/ExampleInstrumentedTest.java
    }
}