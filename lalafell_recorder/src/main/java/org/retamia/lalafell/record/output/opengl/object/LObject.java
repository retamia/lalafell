package org.retamia.lalafell.record.output.opengl.object;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.retamia.lalafell.record.output.opengl.base.Vector;

public class LObject {
    private Vector position;
    private Quaternionf rotate;

    public final Vector getPosition() {
        return position;
    }

    public final void setPosition(Vector position) {
        this.position = position;
    }

    public Quaternionf getRotate() {
        return rotate;
    }

    public void setRotate(Quaternionf rotate) {
        this.rotate = rotate;
    }

    public void setRotate(float angle, float x, float y, float z) {
        this.rotate = new Quaternionf(new AxisAngle4f(angle, x, y, z));
    }

    public void setRotate(AxisAngle4f angle4f) {
        this.rotate = new Quaternionf(angle4f);
    }
}
