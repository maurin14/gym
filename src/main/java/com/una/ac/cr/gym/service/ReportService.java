package com.una.ac.cr.gym.service;








import com.una.ac.cr.gym.domain.Report;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.Payment;
import com.una.ac.cr.gym.repository.ReportRepository;
import com.una.ac.cr.gym.repository.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class ReportService implements CRUD<Report>{
    
    @Autowired
    private ReportRepository rData;
    
    @Autowired
    private UserRepository uData;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    public void save(Report r){
        String validation = validate(r);
        if(validation != null){
        }

        if(r.getReportId() == null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            r.setGenerationDate(LocalDateTime.now().format(formatter));
        }else{
            Report currentReport = getReportById(r.getReportId());
            if(currentReport != null){
                r.setGenerationDate(currentReport.getGenerationDate());
            }
        }

        rData.save(r);
    }

    public void delete(int id){
        Report report = getReportById(id);

        if(report == null){
        }

        rData.deleteById(id);
    }
    
    @Override
    public List<Report> getAll() {
        return getReports();
    }

    @Override
    public Report getById(int id) {
        return getReportById(id);
    }

    public List<Report> getReports(){
        List<Report> reports = rData.findAll();

        for(Report r : reports){
            User u = uData.findById(r.getGeneratedBy()).orElse(null);
            if(u != null){
                r.setUserName(u.getFullName());
            }
        }

        return reports;
    }
    
    public List<Report> filterReports(String reportType, String reportStatus){
        boolean hasType = reportType != null && !reportType.trim().isEmpty();
        boolean hasStatus = reportStatus != null && !reportStatus.trim().isEmpty();

        List<Report> reports;

        if(hasType && hasStatus){
            reports = rData.findByReportTypeAndReportStatus(reportType, reportStatus);
        }else if(hasType){
            reports = rData.findByReportType(reportType);
        }else if(hasStatus){
            reports = rData.findByReportStatus(reportStatus);
        }else{
            reports = rData.findAll();
        }

        for(Report r : reports){
            User u = uData.findById(r.getGeneratedBy()).orElse(null);
            if(u != null){
                r.setUserName(u.getFullName());
            }
        }

        return reports;
    }
    
    public Page<Report> getReportsByPage(int page, int size){
        Page<Report> reportPage = rData.findAll(PageRequest.of(page - 1, size));

        for(Report r : reportPage.getContent()){
            User u = uData.findById(r.getGeneratedBy()).orElse(null);
            if(u != null){
                r.setUserName(u.getFullName());
            }
        }

        return reportPage;
    }

    public Report getReportById(int id){
        Report r = rData.findById(id).orElse(null);

        if(r != null){
            User u = uData.findById(r.getGeneratedBy()).orElse(null);
            if(u != null){
                r.setUserName(u.getFullName());
            }
        }

        return r;
    }

    public String validate(Report r){
        if(r == null){
            return "Datos inválidos";
        }
        
        if(isEmpty(r.getReportType()) || r.getGeneratedBy() == null
                || isEmpty(r.getDescription()) || isEmpty(r.getStartDate()) || isEmpty(r.getEndDate())
                || isEmpty(r.getFormat()) || isEmpty(r.getReportStatus())){
            return "Debe completar todos los campos obligatorios";
        }
        if(r.getStartDate().compareTo(r.getEndDate()) > 0){
            return "La fecha de inicio no puede ser mayor que la fecha final";
        }
        if(!r.getReportType().equals("users") && !r.getReportType().equals("payments") 
                && !r.getReportType().equals("attendances")){
            return "El tipo de reporte debe ser usuarios, pagos o asistencias";
        }
        if(!r.getFormat().equals("pdf") && !r.getFormat().equals("excel") 
                && !r.getFormat().equals("csv")){
            return "El formato debe ser PDF, Excel o CSV";
        }
        if(!r.getReportStatus().equals("generated") && !r.getReportStatus().equals("pending") 
                && !r.getReportStatus().equals("error")){
            return "El estado del reporte debe ser generado, pendiente o error";
        }
        return null;
    }

    public List<Map<String, String>> getPreviewData(String reportType){
        List<Map<String, String>> list = new ArrayList<>();

        if("users".equalsIgnoreCase(reportType)){
            List<User> users = uData.findAll();

            for(User u : users){
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Nombre", u.getFullName());
                map.put("Cédula", u.getIdCard());
                map.put("Correo", u.getEmail());
                map.put("Teléfono", u.getPhone());
                map.put("Usuario", u.getUsername());
                map.put("Rol", u.getRole());
                map.put("Estado", u.getStatus());
                map.put("Fecha de registro", u.getFormattedRecordDate());
                list.add(map);
            }

        }else if("payments".equalsIgnoreCase(reportType)){
            List<Payment> payments = paymentService.getAll();

            for(Payment p : payments){
                Map<String, String> map = new LinkedHashMap<>();

                map.put("ID Pago", String.valueOf(p.getId()));
                map.put("Usuario ID", String.valueOf(p.getUserId()));
                map.put("Monto", "₡" + p.getAmount());
                map.put("Fecha de pago", String.valueOf(p.getPaymentDate()));
                map.put("Método de pago", p.getPaymentMethod());
                map.put("Estado", p.getStatus());
                map.put("Descripción", p.getDescription());

                if(p.getBranch() != null){
                    map.put("Sucursal", String.valueOf(p.getBranch().getId()));
                }else{
                    map.put("Sucursal", "Sin sucursal");
                }

                list.add(map);
            }   list.add(row("Cliente", "María Solano", "Monto", "₡15 000", "Estado", "Pagado"));

        }else if("attendances".equalsIgnoreCase(reportType)){

            List<Attendance> attendances = attendanceService.getAllAttendances();

            for(Attendance a : attendances){

                Map<String, String> map = new LinkedHashMap<>();

                if(a.getClient() != null){
                    map.put("Cliente", a.getClient().getFullName());
                }else{
                    map.put("Cliente", "Sin cliente");
                }

                if(a.getGymClass() != null){
                    map.put("Clase", a.getGymClass().getClassType());
                }else{
                    map.put("Clase", "Sin clase");
                }

                map.put("Fecha", String.valueOf(a.getAttendanceDate()));
                map.put("Asistencia", a.getAttendanceStatus());

                if(a.getObservation() != null){
                    map.put("Observación", a.getObservation());
                }else{
                    map.put("Observación", "Sin observación");
                }

                list.add(map);
            }
        }

        return list;
    }

    private Map<String, String> row(String k1, String v1, String k2, String v2, String k3, String v3){
        Map<String, String> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    private boolean isEmpty(String text){
        return text == null || text.trim().isEmpty();
    }
    
    public Report createNewReport(HttpSession session){
       Report r = new Report();

       User u = (User) session.getAttribute("user");

       if(u != null){
           r.setGeneratedBy(u.getUserId());
           r.setUserName(u.getFullName());
       }

       r.setReportStatus("pending");

       return r;
    }
    
    public boolean isAdministrator(HttpSession session){
        User u = (User) session.getAttribute("user");
        return u != null && "administrator".equals(u.getRole());
    }

    public String validateAdministratorAccess(HttpSession session){
        if(!isAdministrator(session)){
            return "Solo los administradores pueden gestionar reportes";
        }
        return null;
    }
    
    public ResponseEntity<byte[]> downloadReport(Integer id){
        Report report = getReportById(id);

        if(report == null){
            return ResponseEntity.notFound().build();
        }

        if("csv".equals(report.getFormat())){
            return downloadCsv(report);
        }

        if("excel".equals(report.getFormat())){
            return downloadExcel(report);
        }

        if("pdf".equals(report.getFormat())){
            return downloadPdf(report);
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<byte[]> downloadCsv(Report report){
        StringBuilder csv = new StringBuilder();

        csv.append("Reporte del Sistema\n");
        csv.append("Tipo,").append(report.getReportType()).append("\n");
        csv.append("Generado por,").append(report.getUserName()).append("\n");
        csv.append("Fecha de generación,").append(report.getFormattedGenerationDate()).append("\n");
        csv.append("Fecha inicio,").append(report.getFormattedStartDate()).append("\n");
        csv.append("Fecha final,").append(report.getFormattedEndDate()).append("\n");
        csv.append("Formato,").append(report.getFormat()).append("\n");
        csv.append("Estado,").append(report.getReportStatus()).append("\n\n");

        csv.append("Contenido del reporte\n");

        List<Map<String, String>> data = getPreviewData(report.getReportType());

        if(data != null && !data.isEmpty()){
            Map<String, String> firstRow = data.get(0);

            for(String key : firstRow.keySet()){
                csv.append(key).append(",");
            }
            csv.deleteCharAt(csv.length() - 1);
            csv.append("\n");

            for(Map<String, String> row : data){
                for(String value : row.values()){
                    csv.append(value).append(",");
                }
                csv.deleteCharAt(csv.length() - 1);
                csv.append("\n");
            }
        }else{
            csv.append("No hay datos disponibles para este reporte.\n");
        }

        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
        String fileName = "reporte_" + report.getReportId() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
    
    private ResponseEntity<byte[]> downloadExcel(Report report){
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Reporte");

            int rowIndex = 0;

            Row title = sheet.createRow(rowIndex++);
            title.createCell(0).setCellValue("Reporte del Sistema");

            rowIndex++;

            Row r1 = sheet.createRow(rowIndex++);
            r1.createCell(0).setCellValue("Tipo");
            r1.createCell(1).setCellValue(report.getReportType());

            Row r2 = sheet.createRow(rowIndex++);
            r2.createCell(0).setCellValue("Generado por");
            r2.createCell(1).setCellValue(report.getUserName());

            Row r3 = sheet.createRow(rowIndex++);
            r3.createCell(0).setCellValue("Fecha de generación");
            r3.createCell(1).setCellValue(report.getFormattedGenerationDate());

            Row r4 = sheet.createRow(rowIndex++);
            r4.createCell(0).setCellValue("Fecha inicio");
            r4.createCell(1).setCellValue(report.getFormattedStartDate());

            Row r5 = sheet.createRow(rowIndex++);
            r5.createCell(0).setCellValue("Fecha final");
            r5.createCell(1).setCellValue(report.getFormattedEndDate());

            Row r6 = sheet.createRow(rowIndex++);
            r6.createCell(0).setCellValue("Formato");
            r6.createCell(1).setCellValue(report.getFormat());

            Row r7 = sheet.createRow(rowIndex++);
            r7.createCell(0).setCellValue("Estado");
            r7.createCell(1).setCellValue(report.getReportStatus());

            rowIndex++;

            Row contentTitle = sheet.createRow(rowIndex++);
            contentTitle.createCell(0).setCellValue("Contenido del reporte");

            List<Map<String, String>> data = getPreviewData(report.getReportType());

            if(data != null && !data.isEmpty()){
                Map<String, String> firstRow = data.get(0);

                Row header = sheet.createRow(rowIndex++);
                int columnIndex = 0;

                for(String key : firstRow.keySet()){
                    header.createCell(columnIndex++).setCellValue(key);
                }

                for(Map<String, String> map : data){
                    Row dataRow = sheet.createRow(rowIndex++);
                    columnIndex = 0;

                    for(String value : map.values()){
                        dataRow.createCell(columnIndex++).setCellValue(value);
                    }
                }
            }else{
                Row emptyRow = sheet.createRow(rowIndex++);
                emptyRow.createCell(0).setCellValue("No hay datos disponibles para este reporte.");
            }

            for(int i = 0; i < 10; i++){
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);
            workbook.close();

            String fileName = "reporte_" + report.getReportId() + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(output.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private ResponseEntity<byte[]> downloadPdf(Report report){
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();

            document.add(new Paragraph("Reporte del Sistema"));
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            infoTable.addCell("Tipo");
            infoTable.addCell(report.getReportType());

            infoTable.addCell("Generado por");
            infoTable.addCell(report.getUserName());

            infoTable.addCell("Fecha de generación");
            infoTable.addCell(report.getFormattedGenerationDate());

            infoTable.addCell("Fecha inicio");
            infoTable.addCell(report.getFormattedStartDate());

            infoTable.addCell("Fecha final");
            infoTable.addCell(report.getFormattedEndDate());

            infoTable.addCell("Formato");
            infoTable.addCell(report.getFormat());

            infoTable.addCell("Estado");
            infoTable.addCell(report.getReportStatus());

            document.add(infoTable);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Contenido del reporte"));
            document.add(new Paragraph(" "));

            List<Map<String, String>> data = getPreviewData(report.getReportType());

            if(data != null && !data.isEmpty()){
                Map<String, String> firstRow = data.get(0);

                PdfPTable dataTable = new PdfPTable(firstRow.size());
                dataTable.setWidthPercentage(100);

                for(String key : firstRow.keySet()){
                    dataTable.addCell(key);
                }

                for(Map<String, String> row : data){
                    for(String value : row.values()){
                        dataTable.addCell(value);
                    }
                }

                document.add(dataTable);
            }else{
                document.add(new Paragraph("No hay datos disponibles para este reporte."));
            }

            document.close();

            String fileName = "reporte_" + report.getReportId() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(output.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}