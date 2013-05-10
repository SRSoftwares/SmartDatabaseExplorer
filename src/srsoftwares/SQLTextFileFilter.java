package srsoftwares;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: 5/25/12
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLTextFileFilter extends FileFilter {
    String type;

    public SQLTextFileFilter(String type) {
        this.type = type;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } else {
            String fileExt = MiscUtility.getFileExtension(f);
            if (fileExt != null) {
                if (fileExt.equals("txt") && type.equals("Text")) {
                    return true;
                } else if (fileExt.equals("sql") && type.equals("Sql")) {
                    return true;
                } else {
                    return false;
                }

            }
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        if (type.equals("Text")) {
            return "Text Files";
        } else {
            return "SQL Files";
        }

    }
//    public String getTypeDescription(File f) {
//       if(f.isDirectory()){
//        return "";
//    } else{
//        String fileExt= MiscUtility.getFileExtension(f);
//        return fileExt;
//    }
//
//}
}
