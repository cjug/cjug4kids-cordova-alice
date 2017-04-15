// <editor-fold defaultstate="collapsed" desc="imports">

import org.lgna.story.resources.sims2.ChildPersonResource;
// </editor-fold>

class ChildPerson extends Person {

    /* Construct new ChildPerson */
    public ChildPerson(ChildPersonResource resource) {
        super(resource);
    }

    public void setChildPersonResource(ChildPersonResource childPersonResource) {
        this.setJointedModelResource(childPersonResource);
    }
}
